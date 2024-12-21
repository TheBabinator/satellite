package satellite.runtime.state;

import satellite.bytecode.Chunk;
import satellite.bytecode.Instruction;
import satellite.bytecode.Prototype;
import satellite.bytecode.Upvalue;
import satellite.runtime.*;
import satellite.runtime.values.ClosureValue;
import satellite.runtime.values.TableValue;
import satellite.runtime.values.Value;

import java.util.ArrayList;
import java.util.List;

public class InterpretingState extends State {
    private final int recursionLeft;

    private InterpretingState caller;
    private ClosureValue closure;
    private List<Value> extraArguments;

    private ValueStack stack = new ValueStack(1024);
    private int instructionPointer = -1;
    private List<Value> returnValues = null;

    private State callState = null;
    private int callReturns = 0;
    private int callTarget = 0;
    private int tailcallMode = 0;

    public InterpretingState(ClosureValue closure, List<Value> arguments) {
        this.recursionLeft = 1024;
        this.caller = null;
        this.closure = closure;
        this.extraArguments = arguments;
    }

    public InterpretingState(ClosureValue closure, InterpretingState caller, List<Value> arguments) throws SatelliteException {
        this.recursionLeft = caller.recursionLeft - 1;
        if (this.recursionLeft == 0) {
            throw new SatelliteException("too much recursion");
        }
        this.caller = caller;
        this.closure = closure;
        this.extraArguments = arguments;
    }

    public String getLocation() {
        Prototype prototype = closure.getPrototype();
        Chunk chunk = prototype.getChunk();
        if (caller == null) {
            return String.format("%s:%s: in main chunk", chunk.getName(), prototype.getLineString(instructionPointer));
        } else {
            return String.format("%s:%s: in %s", chunk.getName(), prototype.getLineString(instructionPointer), caller.identifyLastCall());
        }
    }

    public String getTraceback() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("call traceback:");
        for (InterpretingState state = this; state != null; state = state.caller) {
            stringBuilder.append("\n\t");
            stringBuilder.append(state.getLocation());
        }
        return stringBuilder.toString();
    }

    public String identifyLastCall() {
        return "?";
    }

    public String identifyArgument(int index) {
        return "?";
    }

    public String identifyThis() {
        return "?";
    }

    public String identifyOther() {
        return "?";
    }

    @Override
    public List<Value> getReturnValues() {
        return returnValues;
    }

    @Override
    public void tick() {
        try {
            if (!doCall()) {
                doInstruction();
            }
        } catch (SatelliteException satelliteException) {
            throw new SatelliteRuntimeException(satelliteException, getTraceback());
        }
    }

    private List<Value> getCallArguments(int index, int count) throws SatelliteException {
        List<Value> arguments = new ArrayList<>();
        if (count >= 0) {
            int last = index + count;
            for (int i = index; i < last; i++) {
                arguments.add(stack.getValue(i));
            }
        } else {
            for (int i = index; stack.hasValue(i); i++) {
                arguments.add(stack.getValue(i));
            }
        }
        return arguments;
    }

    private void setCall(int index, int count, State callState) {
        this.callTarget = index;
        this.callReturns = count;
        this.callState = callState;
    }

    private boolean doCall() throws SatelliteException {
        if (callState == null) {
            return false;
        }
        List<Value> callValues = callState.getReturnValues();
        if (callValues == null) {
            callState.tick();
            return true;
        }
        if (tailcallMode == 2) {
            returnValues = callValues;
            return true;
        }
        if (callReturns < 0) {
            callReturns = callValues.size();
        }
        for (int i = 0; i < callReturns; i++) {
            stack.setValue(callTarget + i, callValues.get(i));
        }
        return false;
    }

    private void doInstruction() throws SatelliteException {
        Prototype prototype = closure.getPrototype();
        List<ValueHolder> upvalues = closure.getUpvalues();
        List<Prototype> children = prototype.getChildren();
        List<Value> constants = prototype.getConstants();
        List<Instruction> instructions = prototype.getInstructions();
        instructionPointer++;
        if (instructionPointer < 0 || instructionPointer >= instructions.size()) {
            throw new SatelliteException("instruction pointer out of range");
        }
        Instruction instruction = instructions.get(instructionPointer);
        List<Integer> operands = instruction.getOperands();
        switch (instruction.getOperationType()) {
            case MOVE -> {
                stack.setValue(operands.get(0), stack.getValue(operands.get(1)));
            }
            case GET_NIL -> {
                stack.setValue(operands.get(0), Value.NIL);
            }
            case GET_FALSE -> {
                stack.setValue(operands.get(0), Value.FALSE);
            }
            case GET_FALSE_SKIP -> {
                stack.setValue(operands.get(0), Value.FALSE);
                instructionPointer++;
            }
            case GET_TRUE -> {
                stack.setValue(operands.get(0), Value.TRUE);
            }
            case GET_TRUE_SKIP -> {
                stack.setValue(operands.get(0), Value.TRUE);
                instructionPointer++;
            }
            case GET_CONSTANT -> {
                if (operands.get(1) < 0 || operands.get(1) >= constants.size()) {
                    throw new SatelliteException("constant index out of range");
                }
                stack.setValue(operands.get(0), constants.get(operands.get(1)));
            }
            case GET_UPVALUE -> {
                if (operands.get(1) < 0 || operands.get(1) >= upvalues.size()) {
                    throw new SatelliteException("upvalue index out of range");
                }
                stack.setValue(operands.get(0), upvalues.get(operands.get(1)).getValue());
            }
            case GET_TABLE -> {
                setCall(operands.get(0), 1,
                        stack.getValue(operands.get(1)).callGetIndex(this, stack.getValue(operands.get(2))));
            }
            case SET_UPVALUE -> {
                if (operands.get(1) < 0 || operands.get(1) >= upvalues.size()) {
                    throw new SatelliteException("upvalue index out of range");
                }
                upvalues.get(operands.get(1)).setValue(stack.getValue(operands.get(0)));
            }
            case SET_TABLE -> {
                setCall(0, 0,
                        stack.getValue(operands.get(1)).callPutIndex(this, stack.getValue(operands.get(2)), stack.getValue(operands.get(0))));
            }
            case NEW_TABLE -> {
                stack.setValue(operands.get(0), new TableValue());
            }
            case NEW_CLOSURE -> {
                if (operands.get(1) < 0 || operands.get(1) >= children.size()) {
                    throw new SatelliteException("prototype index out of range");
                }
                Prototype childPrototype = children.get(operands.get(1));
                List<ValueHolder> childUpvalues = new ArrayList<>();
                for (Upvalue upvalue : childPrototype.getUpvalues()) {
                    if (upvalue.isNested()) {
                        if (upvalue.getIndex() < 0 || upvalue.getIndex() >= upvalues.size()) {
                            throw new SatelliteException("upvalue index out of range");
                        }
                        childUpvalues.add(upvalues.get(upvalue.getIndex()));
                    } else {
                        childUpvalues.add(stack.copy(upvalue.getIndex()));
                    }
                }
                stack.setValue(operands.get(0), new ClosureValue(childPrototype, childUpvalues));
            }
            case CONCAT -> {
                setCall(operands.get(0), 1,
                        stack.getValue(operands.get(1)).callConcat(this, stack.getValue(operands.get(2))));
            }
            case CALL -> {
                setCall(operands.get(0), operands.get(2) - 1,
                        stack.getValue(operands.get(0)).call(this, getCallArguments(operands.get(0) + 1, operands.get(1) - 1)));
            }
            case TAILCALL -> {
                State newState = stack.getValue(operands.get(0)).call(this, getCallArguments(operands.get(0) + 1, operands.get(1) - 1));
                if (newState instanceof InterpretingState newInterpretingState) {
                    closure = newInterpretingState.closure;
                    extraArguments = newInterpretingState.extraArguments;
                    stack = newInterpretingState.stack;
                    instructionPointer = newInterpretingState.instructionPointer;
                    returnValues = newInterpretingState.returnValues;
                    callState = newInterpretingState.callState;
                    callReturns = newInterpretingState.callReturns;
                    callTarget = newInterpretingState.callTarget;
                    tailcallMode = 1;
                } else {
                    callState = newState;
                    tailcallMode = 2;
                }
            }
            case RETURN -> {
                returnValues = getCallArguments(operands.get(0), operands.get(1) - 1);
            }
            case TEST_FALSE -> {
                if (stack.getValue(operands.get(0)).toBoolean()) {
                    instructionPointer++;
                }
            }
            case TEST_TRUE -> {
                if (!stack.getValue(operands.get(0)).toBoolean()) {
                    instructionPointer++;
                }
            }
            case JUMP -> {
                instructionPointer += operands.get(0);
            }
        }
    }
}

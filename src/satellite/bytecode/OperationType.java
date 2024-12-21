package satellite.bytecode;

import java.util.Arrays;
import java.util.List;

public enum OperationType {
    NOTHING,
    MOVE(OperandType.STACK, OperandType.STACK),

    GET_NIL(OperandType.STACK),
    GET_FALSE(OperandType.STACK),
    GET_FALSE_SKIP(OperandType.STACK),
    GET_TRUE(OperandType.STACK),
    GET_TRUE_SKIP(OperandType.STACK),
    GET_CONSTANT(OperandType.STACK, OperandType.CONSTANT),
    GET_UPVALUE(OperandType.STACK, OperandType.CONSTANT),
    GET_TABLE(OperandType.STACK, OperandType.STACK, OperandType.STACK),

    SET_UPVALUE(OperandType.STACK, OperandType.CONSTANT),
    SET_TABLE(OperandType.STACK, OperandType.STACK, OperandType.STACK),

    NEW_TABLE(OperandType.STACK),
    NEW_CLOSURE(OperandType.STACK, OperandType.CONSTANT),

    CONCAT(OperandType.STACK, OperandType.STACK, OperandType.STACK),

    CALL(OperandType.STACK, OperandType.STACK, OperandType.STACK),
    TAILCALL(OperandType.STACK, OperandType.STACK),
    RETURN(OperandType.STACK, OperandType.STACK),

    TEST_FALSE(OperandType.STACK),
    TEST_TRUE(OperandType.STACK),
    JUMP(OperandType.JUMP);

    private final List<OperandType> operandTypes;

    OperationType(OperandType... operandTypes) {
        this.operandTypes = Arrays.stream(operandTypes).toList();
    }

    public List<OperandType> getOperandTypes() {
        return operandTypes;
    }
}

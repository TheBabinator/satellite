package satellite.runtime.values;

import satellite.bytecode.Prototype;
import satellite.runtime.SatelliteException;
import satellite.runtime.ValueHolder;
import satellite.runtime.state.InterpretingState;
import satellite.runtime.state.State;

import java.util.List;

public class ClosureValue extends FunctionValue {
    private final Prototype prototype;
    private final List<ValueHolder> upvalues;

    public ClosureValue(Prototype prototype, List<ValueHolder> upvalues) {
        this.prototype = prototype;
        this.upvalues = upvalues;
    }

    public Prototype getPrototype() {
        return prototype;
    }

    public List<ValueHolder> getUpvalues() {
        return upvalues;
    }

    @Override
    public State call(InterpretingState caller, List<Value> arguments) throws SatelliteException {
        return new InterpretingState(this, caller, arguments);
    }
}

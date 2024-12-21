package satellite.runtime.values;

import satellite.runtime.SatelliteException;
import satellite.runtime.state.InterpretingState;
import satellite.runtime.state.State;

import java.util.List;

public abstract class FunctionValue extends Value {
    @Override
    public String getTypeName() {
        return "function";
    }

    @Override
    public abstract State call(InterpretingState caller, List<Value> arguments) throws SatelliteException;
}

package satellite.runtime.values;

import satellite.runtime.SatelliteException;
import satellite.runtime.state.InterpretingState;
import satellite.runtime.state.State;

public class StringValue extends Value {
    private final String string;

    public StringValue(String string) {
        this.string = string;
    }

    @Override
    public String getTypeName() {
        return "number";
    }

    @Override
    public String toNiceString() {
        return string;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", string);
    }

    @Override
    public State callConcat(InterpretingState caller, Value subject) throws SatelliteException {
        return super.callConcat(caller, subject);
    }
}

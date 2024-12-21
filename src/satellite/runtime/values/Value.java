package satellite.runtime.values;

import satellite.runtime.SatelliteException;
import satellite.runtime.state.InterpretingState;
import satellite.runtime.state.State;

import java.util.List;

public abstract class Value {
    public static final Value NIL = new Value() {
        @Override
        public String getTypeName() {
            return "nil";
        }

        @Override
        public boolean toBoolean() {
            return false;
        }

        @Override
        public String toString() {
            return "nil";
        }
    };

    public static final Value FALSE = new Value() {
        @Override
        public String getTypeName() {
            return "boolean";
        }

        @Override
        public boolean toBoolean() {
            return false;
        }

        @Override
        public String toString() {
            return "false";
        }
    };

    public static final Value TRUE = new Value() {
        @Override
        public String getTypeName() {
            return "boolean";
        }

        @Override
        public String toString() {
            return "true";
        }
    };

    public static Value orNil(Value value) {
        return value == null ? NIL : value;
    }

    public static Value orNull(Value value) {
        return value == NIL ? null : value;
    }

    public abstract String getTypeName();

    public boolean toBoolean() {
        return true;
    }

    public String toNiceString() {
        return toString();
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    public State call(InterpretingState caller, List<Value> arguments) throws SatelliteException {
        throw new SatelliteException("attempt to call a %s value (%s)", getTypeName(), caller.identifyLastCall());
    }

    public State callPutIndex(InterpretingState caller, Value key, Value value) throws SatelliteException {
        throw new SatelliteException("attempt to index a %s value (%s)", getTypeName(), caller.identifyLastCall());
    }

    public State callGetIndex(InterpretingState caller, Value key) throws SatelliteException {
        throw new SatelliteException("attempt to index a %s value (%s)", getTypeName(), caller.identifyLastCall());
    }

    public State callConcat(InterpretingState caller, Value other) throws SatelliteException {
        throw new SatelliteException("attempt to concatenate %s (%s) with %s (%s)", getTypeName(), caller.identifyThis(), other.getTypeName(), caller.identifyOther());
    }
}

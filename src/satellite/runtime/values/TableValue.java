package satellite.runtime.values;

import satellite.runtime.SatelliteException;
import satellite.runtime.state.InterpretingState;
import satellite.runtime.state.State;

import java.util.HashMap;
import java.util.Map;

public class TableValue extends Value {
    private final Map<Value, Value> map = new HashMap<>();

    @Override
    public String getTypeName() {
        return "table";
    }

    public void put(Value key, Value value) {
        map.put(key, orNull(value));
    }

    public Value get(Value key) {
        return orNil(map.get(key));
    }

    public void put(String key, Value value) {
        map.put(new StringValue(key), value);
    }

    public Value get(String key) {
        return orNil(map.get(new StringValue(key)));
    }

    @Override
    public State callPutIndex(InterpretingState caller, Value key, Value value) throws SatelliteException {
        return super.callPutIndex(caller, key, value);
    }

    @Override
    public State callGetIndex(InterpretingState caller, Value key) throws SatelliteException {
        return super.callGetIndex(caller, key);
    }
}

package satellite.runtime;

import satellite.runtime.values.Value;

public class ValueHolder {
    private Value value;

    public ValueHolder() {

    }

    public ValueHolder(Value value) {
        this.value = value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}

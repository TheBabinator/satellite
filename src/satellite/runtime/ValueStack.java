package satellite.runtime;

import satellite.runtime.values.Value;

public class ValueStack {
    ValueHolder[] valueHolders;

    public ValueStack(int size) {
        this.valueHolders = new ValueHolder[size];
    }

    public void checkIndex(int index) throws SatelliteException {
        if (index < 0 || index >= valueHolders.length) {
            throw new SatelliteException("stack overflow");
        }
    }

    public boolean hasValue(int index) throws SatelliteException {
        checkIndex(index);
        ValueHolder valueHolder = valueHolders[index];
        if (valueHolder == null) {
            return false;
        }
        return valueHolder.getValue() != null;
    }

    public void setValue(int index, Value value) throws SatelliteException {
        checkIndex(index);
        ValueHolder valueHolder = valueHolders[index];
        if (valueHolder != null) {
            valueHolder.setValue(value);
        } else {
            valueHolders[index] = new ValueHolder(value);
        }
    }

    public Value getValue(int index) throws SatelliteException {
        checkIndex(index);
        ValueHolder valueHolder = valueHolders[index];
        if (valueHolder != null) {
            Value value = valueHolder.getValue();
            if (value != null) {
                return value;
            }
        }
        throw new SatelliteException("bad stack access");
    }

    public ValueHolder copy(int index) throws SatelliteException {
        checkIndex(index);
        ValueHolder valueHolder = valueHolders[index];
        if (valueHolder == null) {
            valueHolder = new ValueHolder();
            valueHolders[index] = valueHolder;
        }
        return valueHolder;
    }

    public void closeFrom(int index) throws SatelliteException {
        checkIndex(index);
        for (int i = index; i < valueHolders.length; i++) {
            valueHolders[i] = null;
        }
    }
}

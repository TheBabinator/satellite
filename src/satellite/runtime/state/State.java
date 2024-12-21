package satellite.runtime.state;

import satellite.runtime.values.Value;

import java.util.List;

public abstract class State {
    public abstract List<Value> getReturnValues();
    public abstract void tick();

    public List<Value> run() {
        while (true) {
            List<Value> returnValues = getReturnValues();
            if (returnValues == null) tick();
            else return returnValues;
        }
    }
}

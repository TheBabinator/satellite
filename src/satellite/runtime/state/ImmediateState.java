package satellite.runtime.state;

import satellite.runtime.ValueHolder;
import satellite.runtime.values.Value;

import java.util.Arrays;
import java.util.List;

public class ImmediateState extends State {
    private final List<Value> returnValues;

    public ImmediateState(Value... returnValues) {
        this.returnValues = Arrays.stream(returnValues).toList();
    }

    @Override
    public List<Value> getReturnValues() {
        return returnValues;
    }

    @Override
    public void tick() {

    }
}

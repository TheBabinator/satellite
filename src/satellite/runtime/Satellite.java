package satellite.runtime;

import satellite.bytecode.Chunk;
import satellite.runtime.state.InterpretingState;
import satellite.runtime.state.State;
import satellite.runtime.values.ClosureValue;
import satellite.runtime.values.TableValue;
import satellite.runtime.values.Value;

import java.util.List;

public class Satellite {
    private final TableValue environment = new TableValue();
    private State state;

    public Satellite() {

    }

    public void loadChunk(Chunk chunk, List<Value> arguments) {
        ValueHolder upvalue = new ValueHolder(environment);
        ClosureValue closure = new ClosureValue(chunk.getRoot(), List.of(upvalue));
        state = new InterpretingState(closure, arguments);
    }

    public void tick() {
        if (state == null) {
            throw new IllegalStateException("nothing to do");
        }
        state.tick();
    }

    public List<Value> run() {
        if (state == null) {
            throw new IllegalStateException("nothing to do");
        }
        return state.run();
    }
}

import satellite.bytecode.Chunk;
import satellite.bytecode.Instruction;
import satellite.bytecode.OperationType;
import satellite.bytecode.Prototype;
import satellite.runtime.Satellite;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Prototype prototype = new Prototype();
        prototype.getInstructions().addAll(List.of(
                new Instruction(OperationType.GET_TRUE, List.of(0)),
                new Instruction(OperationType.CALL, List.of(0, 0, 0)),
                new Instruction(OperationType.RETURN, List.of(0, 0))
        ));
        prototype.getLineNumbers().addAll(List.of(1, 2, 3));
        Chunk chunk = new Chunk("test", prototype);
        Satellite satellite = new Satellite();
        satellite.loadChunk(chunk, List.of());
        System.out.println(satellite.run());
    }
}

package satellite.bytecode;

import satellite.runtime.values.Value;

import java.util.ArrayList;
import java.util.List;

public class Prototype {
    private Chunk chunk;
    private final List<Prototype> children = new ArrayList<>();
    private final List<Upvalue> upvalues = new ArrayList<>();
    private final List<Value> constants = new ArrayList<>();
    private final List<Instruction> instructions = new ArrayList<>();
    private final List<Integer> lineNumbers = new ArrayList<>();

    public Prototype() {

    }

    public Prototype(Prototype parent) {
        parent.getChildren().add(this);
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
        for (Prototype child : children) {
            child.setChunk(chunk);
        }
    }

    public Chunk getChunk() {
        return chunk;
    }

    public List<Prototype> getChildren() {
        return children;
    }

    public List<Upvalue> getUpvalues() {
        return upvalues;
    }

    public List<Value> getConstants() {
        return constants;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<Integer> getLineNumbers() {
        return lineNumbers;
    }

    public String getLineString(int instructionPointer) {
        if (instructionPointer < 0 || instructionPointer >= lineNumbers.size()) {
            return "?";
        }
        return Integer.toUnsignedString(lineNumbers.get(instructionPointer));
    }
}

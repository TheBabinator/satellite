package satellite.bytecode;

public class Chunk {
    private final String name;
    private final Prototype root;

    public Chunk(String name, Prototype root) {
        this.name = name;
        this.root = root;
        this.root.setChunk(this);
    }

    public String getName() {
        return name;
    }

    public Prototype getRoot() {
        return root;
    }
}

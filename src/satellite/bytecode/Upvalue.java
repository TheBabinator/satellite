package satellite.bytecode;

public class Upvalue {
    private final String name;
    private final int index;
    private final boolean nested;

    public Upvalue(String name, int index, boolean nested) {
        this.name = name;
        this.index = index;
        this.nested = nested;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isNested() {
        return nested;
    }
}

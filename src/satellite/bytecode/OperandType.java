package satellite.bytecode;

public enum OperandType {
    STACK(1, false),
    CONSTANT(2, false),
    JUMP(4, true);

    private final int size;
    private final boolean signed;

    OperandType(int size, boolean signed) {
        this.size = size;
        this.signed = signed;
    }

    public int getSize() {
        return size;
    }

    public boolean isSigned() {
        return signed;
    }
}

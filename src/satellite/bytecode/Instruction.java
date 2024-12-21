package satellite.bytecode;

import java.util.List;

public class Instruction {
    private final OperationType operationType;
    private final List<Integer> operands;

    public Instruction(OperationType operationType, List<Integer> operands) {
        if (operationType.getOperandTypes().size() != operands.size()) {
            throw new IllegalArgumentException(String.format("Operation %s expects %d operands, got %d", operationType, operationType.getOperandTypes().size(), operands.size()));
        }
        this.operationType = operationType;
        this.operands = operands;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public List<Integer> getOperands() {
        return operands;
    }
}

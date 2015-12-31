package jvm.frame;

import jvm.values.Value;

/**
 *
 * @author Jaroslav Ševčík
 */

public class LocalVariable {
    private Value value;

    public LocalVariable(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
    
}

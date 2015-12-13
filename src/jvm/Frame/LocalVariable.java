package jvm.frame;

import jvm.values.Value;

/**
 *
 * @author Jaroslav Ševčík
 */
//možná nebude třeba
public class LocalVariable {
    //int nameIndex;
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

package jvm.values;

/**
 *
 * @author Jaroslav Ševčík
 */
public abstract class Value {
    public static final int INT_TAG = 1;
    public static final int CHAR_TAG = 2;
    public static final int REFERENCE_TAG = 3;
    public int tag;

    public Value(int tag) {
        this.tag = tag;
    }
    
    public int getTag () {
        return tag;
    }

}

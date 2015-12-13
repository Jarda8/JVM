package jvm;

import java.nio.ByteBuffer;
import org.apache.bcel.classfile.JavaClass;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Heap {

    private static final int HEAP_SIZE = (int) Math.pow(2, 20);
    public final ByteBuffer heap = ByteBuffer.allocate(HEAP_SIZE);
    //Nejdřív se bude všechno alokovat za sebe a adekvátně se posune index prvního volného místa.
    public int firstFree = 0;
    
    public int allocateObject (JavaClass clazz) {
        int ptr = firstFree;
        //ještě tu budou nějaký flagy
        for (int i = 0; i < clazz.getFields().length; i++) {
            firstFree += clazz.getFields()[i].getType().getSize();
        }
        return ptr;
    }
    
    public int alocateArray (int length, int sizeOfElement) {
        int ptr = firstFree;
        heap.putInt(firstFree, length);
        firstFree += length * sizeOfElement + 4;
        return ptr;
    }
}

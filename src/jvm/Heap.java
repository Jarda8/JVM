package jvm;

/**
 *
 * @author Jaroslav Ševčík
 */
public class Heap {

    private static final int HEAP_SIZE = (int) Math.pow(2, 20);
    private byte[] byteArray = new byte[HEAP_SIZE];
}

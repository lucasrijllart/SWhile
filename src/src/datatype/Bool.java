package src.datatype;

/**
 *
 * @author Lucas
 */
public class Bool extends Data {
    private final boolean value;
    
    /**
     *
     * @param s
     */
    public Bool(String s) {
        this.value = s.equals("true");
    }
    
    /**
     *
     * @return
     */
    public boolean getValue() {return this.value;}

    /**
     *
     * @return
     */
    @Override
    public Data getData() {
        if (this.value) {
            return new Tree(new Nil(), new Nil());
        } else {
            return new Nil();
        }
    }
}
package src.datatype;

/**
 *
 * @author Lucas
 */
public class Num extends Data {

    private final Integer value;

    /**
     *
     * @param s
     */
    public Num(String s) {
        this.value = Integer.parseInt(s);
    }

    /**
     *
     * @return
     */
    public Integer getInt() {
        return this.value;
    }

    /**
     *
     * @return
     */
    @Override
    public Data getData() {
        Tree intToTree;
        if (this.value == 0) {
            return new Nil();
        } else {
            intToTree = new Tree(new Nil(), new Nil());
            for (int i = 1; i < value; i++) {
                intToTree = new Tree(new Nil(), intToTree);
            }
        }
        return intToTree;
    }
}

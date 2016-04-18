package src.datatype;

/**
 *
 * @author Lucas
 */
public class Tree extends Data {

    private final Data left; //left node
    private final Data right; //right node

    /**
     *
     * @param l
     * @param r
     */
    public Tree(Data l, Data r) {
        this.left = l;
        this.right = r;
    }

    /**
     *
     * @return
     */
    public Data getLeft() {
        return this.left;
    }

    /**
     *
     * @return
     */
    public Data getRight() {
        return this.right;
    }

    /**
     *
     * @return
     */
    @Override
    public Data getData() {
        return this;
    }
}

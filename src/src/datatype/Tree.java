package src.datatype;

/**
 *
 * @author Lucas
 */
public class Tree extends Data {

    private final Data left; //left node
    private final Data right; //right node

    public Tree(Data l, Data r) {
        this.left = l;
        this.right = r;
    }

    public Data getLeft() {
        return this.left;
    }

    public Data getRight() {
        return this.right;
    }

    @Override
    public Data getData() {
        return this;
    }
}

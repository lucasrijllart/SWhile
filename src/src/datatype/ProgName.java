package src.datatype;

/**
 *
 * @author Lucas
 */
public class ProgName extends Data {

    private final String name;

    /**
     *
     * @param n
     */
    public ProgName(String n) {
        this.name = n;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
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
 
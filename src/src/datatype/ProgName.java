package src.datatype;

/**
 *
 * @author Lucas
 */
public class ProgName extends Data {

    private final String name;

    public ProgName(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }

    @Override
    public Data getData() {
        return this;
    }
}
 
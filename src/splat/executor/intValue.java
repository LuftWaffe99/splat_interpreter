package splat.executor;
import splat.parser.elements.Type;

public class intValue extends Value{

    private int value;

    public intValue(Type type, String val) {
        super(type);
        this.value = Integer.parseInt(val);
    }
    // if Value is not defined before set integer to default 0 value
    public intValue(Type type) {
        super(type);
        this.value = 0;
    }

    public intValue(Type type, int val) {
        super(type);
        this.value = val;
    }


    public String toString() {return "Type: "+ super.getType().toString() + ", Value: " + value;}
    public Type getType() {return super.getType();}
    public String getValue() {return String.valueOf(value);}
    public int convValue() {return value;}
}

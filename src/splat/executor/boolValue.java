package splat.executor;
import splat.parser.elements.Type;

public class boolValue extends Value{

    private boolean value;

    public boolValue(Type type, String val) {
        super(type);
        this.value = Boolean.parseBoolean(val);
    }

    public boolValue(Type type) {
        super(type);
        this.value = false;
    }

    public boolValue(Type type, boolean val) {
        super(type);
        this.value = val;
    }

    public String toString() {return "Type: "+ super.getType().toString() + ", Value: " + value;}
    public Type getType() {return super.getType();}
    public String getValue() {return String.valueOf(value);}
    public boolean convValue() {return value;}
}

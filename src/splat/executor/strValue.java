package splat.executor;
import splat.parser.elements.Type;

public class strValue extends Value{

    private String value;

    public strValue(Type type, String val) {
        super(type);
        this.value = val;
    }

    public strValue(Type type) {
        super(type);
        this.value = "";
    }

    public String toString() {return "Type: "+ super.getType().toString() + ", Value: " + value;}
    public Type getType() {return super.getType();}
    public String getValue() {return this.value;}

}

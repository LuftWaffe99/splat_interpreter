package splat.executor;
import splat.lexer.*;
import splat.parser.elements.Type;

public abstract class Value {

	// TODO: Implement me!
    private Type type;

    public Value(Type type){
        this.type = type;
    }

    public Type getType() {return this.type;}

    public abstract String toString();

    public abstract String getValue();


}

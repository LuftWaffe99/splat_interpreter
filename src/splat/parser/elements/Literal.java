package splat.parser.elements;
import splat.lexer.Token;



public class Literal extends ASTElement{

    private Type type;

    public Literal(Token token, Type type){
        super(token);
        this.type = type;
    }

    public String toString() {return type.toString();}
    public Type getType() {
        return type;
    }

}

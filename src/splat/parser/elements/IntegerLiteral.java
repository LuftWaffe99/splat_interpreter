package splat.parser.elements;
import splat.lexer.Token;
public class IntegerLiteral extends Literal{

    private String value = "0";

    public IntegerLiteral(Token token) {
        super(token, new Type(token, "Integer")); // stores type as Integer
        this.value = token.getValue(); // could be #some_number
    }

    public String toString(){return this.value;}
    public String getValue(){return this.value;}

}

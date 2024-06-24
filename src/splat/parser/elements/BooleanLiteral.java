package splat.parser.elements;

import splat.lexer.Token;

public class BooleanLiteral extends Literal{

    private String value = "false";

    public BooleanLiteral(Token token) {
        super(token, new Type(token, "Boolean")); // stores type as Boolean
        this.value = token.getValue(); // could be true/false
    }

    public String toString(){return this.value;}
    public String getValue(){return this.value;}
}

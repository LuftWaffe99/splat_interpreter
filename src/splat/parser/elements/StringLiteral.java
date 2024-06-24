package splat.parser.elements;

import splat.lexer.Token;

public class StringLiteral extends Literal{

    private String value = "";

    public StringLiteral(Token token, String value) {
        super(token, new Type(token, "String")); // stores type as String
        this.value = value; // could be ".....some text...."
    }

    public String toString(){return this.value;}
    public String getValue(){return this.value;}

}

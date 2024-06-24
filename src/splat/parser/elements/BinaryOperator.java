package splat.parser.elements;
import splat.lexer.Token;


public class BinaryOperator extends ASTElement{

    private String operator;

    public BinaryOperator(Token token) {
        super(token);
        this.operator = token.getValue(); // could be and | or | > | < | == | >= | <= | + | - | * | / | %
    }

    public String toString() {return operator;}
    public String getValue() {
        return operator;
    }

}

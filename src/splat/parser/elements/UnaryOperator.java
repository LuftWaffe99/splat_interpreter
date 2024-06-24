package splat.parser.elements;
import splat.lexer.Token;

public class UnaryOperator extends ASTElement {

    private String operator;

    public UnaryOperator(Token token) {
        super(token);
        this.operator = token.getValue(); // could be not/-
    }

    public String toString() {
        return operator;
    }
    public String getValue() {
        return operator;
    }


}

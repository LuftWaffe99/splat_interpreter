package splat.parser.elements;
import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;


import java.util.Map;

public class LiteralExpression extends Expression {

    private Literal literal;

    public LiteralExpression(Token token, Literal literal) {
        super(token);
        this.literal = literal;
    }

    public String toString() {
        return literal.toString();
    }

    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        return this.literal.getType();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) {
        if (literal instanceof IntegerLiteral) {
            return new intValue(literal.getType(), literal.toString());
        } else if (literal instanceof BooleanLiteral) {
            return new boolValue(literal.getType(), literal.toString());
        } else {
            return new strValue(literal.getType(), literal.toString());
        }
    }
}
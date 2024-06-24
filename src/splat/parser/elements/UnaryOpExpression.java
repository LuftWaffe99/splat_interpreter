package splat.parser.elements;
import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.Map;

public class UnaryOpExpression extends Expression{

    private Token token;
    private UnaryOperator unaryOp;
    private Expression expr;
    private String text;

    public UnaryOpExpression(Token token, UnaryOperator unaryOp, Expression expr) {
        super(token);
        this.token = token;
        this.unaryOp = unaryOp;
        this.expr = expr;
        this.text = " ( " + unaryOp + expr + " ) ";  // Initialize text inside the constructor
    }

    public String toString() {
        return text;
    }

    @Override
    public  Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
                                   Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        String operator = unaryOp.toString();
        Type exprType = expr.analyzeAndGetType(funcMap, varAndParamMap);

        int column = exprType.getColumn();
        int line = exprType.getLine();
        String after = exprType.toString();
        /*
            (  not <expr>  )
         */

        if (operator.equals("not")) {
            if (after.equals("Boolean")) { return new Type(token, "Boolean");}
            else {throw new SemanticAnalysisException("Invalid type for unary expression (Boolean type expected)", line, column);}
        }

        /*
            ( - <expr> )
         */
        if (operator.equals("-") ) {
            if (after.equals("Integer")) {return new Type(token, "Integer");}
            else { throw new SemanticAnalysisException("Invalid type for binary expression (Integer type expected)", line, column);}
        }

        return null;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {

        Value valExpr = expr.evaluate(funcMap, varAndParamMap);
        String operator = unaryOp.toString();

        if (operator.equals("not")) {
            boolValue valCasted = (boolValue) valExpr;
            Type valResultType = new Type(token, "Boolean");
            boolean result = !valCasted.convValue();
            return new boolValue(valResultType, result);

        } else {
            intValue valCasted = (intValue) valExpr;
            Type valResultType = new Type(token, "Integer");
            int result = -(valCasted.convValue());
            return new intValue(valResultType, result);
        }

    }
}

package splat.parser.elements;
import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.Map;


public class BinaryOpExpression extends Expression{

    private Token token;
    private Expression before_expr;
    private BinaryOperator binaryOp;
    private Expression after_expr;
    private String text;

    public BinaryOpExpression(Token token, Expression before_expr, BinaryOperator binaryOp, Expression after_expr) {
        super(token);
        this.token = token;
        this.before_expr = before_expr;
        this.binaryOp = binaryOp;
        this.after_expr = after_expr;
        this.text = " ( " + before_expr + binaryOp + after_expr  +" ) ";
    }

    public String toString() {
        return text;
    }

    @Override
    public  Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
                                   Map<String, Type> varAndParamMap) throws SemanticAnalysisException{

        String operator = binaryOp.toString();
        Type beforeType = before_expr.analyzeAndGetType(funcMap, varAndParamMap);
        Type afterType = after_expr.analyzeAndGetType(funcMap, varAndParamMap);

        int column = beforeType.getColumn();
        int line = beforeType.getLine();
        String before = beforeType.toString();
        String after = afterType.toString();
        /*
            (  <expr>   ==   <expr> )
         */

        if (operator.equals("==")) {

            if (before.equals("Integer") && after.equals("Integer")) {
                return new Type(token, "Boolean");
            } else if (before.equals("String") && after.equals("String")){
                return new Type(token, "Boolean");
            } else if (before.equals("Boolean") && after.equals("Boolean")) {
                return new Type(token, "Boolean");
            }
            throw new SemanticAnalysisException("Invalid type for binary expression", line, column);
        }
        /*
            (  <expr>   and/or   <expr> )
         */
        if (operator.equals("and") || operator.equals("or")) {

            if (before.equals("Boolean") && after.equals("Boolean")) {
                return new Type(token, "Boolean");
            }
            throw new SemanticAnalysisException("Invalid type for binary expression (Boolean types expected)", line, column);
        }
        /*
            (  <expr>   >/</>=/<=   <expr> )
         */

        if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals("<=") ) {

            if (before.equals("Integer") && after.equals("Integer")) {
                return new Type(token, "Boolean");
            }
            throw new SemanticAnalysisException("Invalid type for binary expression (Integer types expected)", line, column);
        }

        /*
           (  <expr>   +/-/"*"/"/"/%  <expr> )
         */

        if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("%") ) {

            if (before.equals("Integer") && after.equals("Integer")) {
                return new Type(token, "Integer");
            }
            throw new SemanticAnalysisException("Invalid type for binary expression (Integer types expected)", line, column);
        }


        return null;
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException{

        Value valLeft = before_expr.evaluate(funcMap, varAndParamMap);
        Value valRight = after_expr.evaluate(funcMap, varAndParamMap);
        String operator = binaryOp.toString();

        if (operator.equals("==")) {
            // Return boolean value --> false/true
            Type valResultType  = new Type(token, "Boolean");
            boolean result = valLeft.getValue().equals(valRight.getValue());
            return new boolValue(valResultType, result);

        } else if (operator.equals("and") || operator.equals("or") ) {
            // Return boolean value --> false/true
            Type valResultType = new Type(token, "Boolean");
            boolValue valLeftCasted = (boolValue) valLeft;
            boolValue valRightCasted = (boolValue) valRight;
            boolean result = false;

            if (operator.equals("and")) {
                result = valLeftCasted.convValue() && valRightCasted.convValue();
                return new boolValue(valResultType, result);
            } else {
                result = valLeftCasted.convValue() || valRightCasted.convValue();
                return new boolValue(valResultType, result);
            }


        } else if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals("<=") ) {
            // Return boolean value --> false/true
            Type valResultType = new Type(token, "Boolean");
            intValue valLeftCasted = (intValue) valLeft;
            intValue valRightCasted = (intValue) valRight;
            boolean result = false;

            if (operator.equals(">")) {
                result = valLeftCasted.convValue() > valRightCasted.convValue();
            } else if (operator.equals("<")) {
                result = valLeftCasted.convValue() < valRightCasted.convValue();
            } else if (operator.equals(">=")) {
                result = valLeftCasted.convValue() >= valRightCasted.convValue();
            } else {
                result = valLeftCasted.convValue() <= valRightCasted.convValue();
            }

            return new boolValue(valResultType, result);
        } else {
            // Return integer value --> int number
            Type valResultType = new Type(token, "Integer");
            intValue valLeftCasted = (intValue) valLeft;
            intValue valRightCasted = (intValue) valRight;
            int result = 0;

            if (operator.equals("+")) {
                result = valLeftCasted.convValue() + valRightCasted.convValue();
            } else if (operator.equals("-")) {
                result = valLeftCasted.convValue() - valRightCasted.convValue();
            } else if (operator.equals("*")) {
                result = valLeftCasted.convValue() * valRightCasted.convValue();
            } else if (operator.equals("/")) {

                if (valRightCasted.convValue() == 0){
                    throw new ExecutionException("Division by zero", after_expr.getLine(), after_expr.getColumn());
                }
                result = valLeftCasted.convValue() / valRightCasted.convValue();

            } else {

                if (valRightCasted.convValue() == 0){
                    throw new ExecutionException("Undefined mod operation", after_expr.getLine(), after_expr.getColumn());
                }
                result = valLeftCasted.convValue() % valRightCasted.convValue();

            }

            return new intValue(valResultType, result);
        }




    }
}

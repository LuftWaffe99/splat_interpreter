package splat.parser.elements;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;
import java.util.Map;
import java.util.List;
import splat.executor.*;


public class While_Statement extends Statement{

    private Expression expr;
    private List<Statement> stmts;

    public While_Statement(Token token, Expression expr, List<Statement> stmts) {
        super(token);
        this.expr = expr;
        this.stmts = stmts;
    }

    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {

        Type exprType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);

        if (!exprType.toString().equals("Boolean")) {
            throw new SemanticAnalysisException("Expression for While loop should be Boolean", expr.getLine(), expr.getColumn());
        }
        // Check other statements
        for (Statement stmt: this.stmts) {
            stmt.analyze(funcMap, varAndParamMap);
        }

    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException  {

        boolValue flag = (boolValue) expr.evaluate(funcMap, varAndParamMap);

        while (flag.convValue()) {
//
            for (Statement stmt : stmts) {
                stmt.execute(funcMap, varAndParamMap);
            }
            flag = (boolValue) expr.evaluate(funcMap, varAndParamMap);
        }
    }
}

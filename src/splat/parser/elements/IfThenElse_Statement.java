package splat.parser.elements;
import splat.executor.ExecutionException;
import splat.executor.ReturnFromCall;
import splat.executor.Value;
import splat.executor.boolValue;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import javax.swing.plaf.nimbus.State;
import java.util.List;
import java.util.Map;


public class IfThenElse_Statement extends Statement{

    private Expression expr;
    private List<Statement> then_stmts;
    private List<Statement> else_stmts;

    public IfThenElse_Statement(Token token, Expression expr, List<Statement> then_stmts, List<Statement> else_stmts) {
        super(token);
        this.expr = expr;
        this.then_stmts = then_stmts;
        this.else_stmts = else_stmts;
    }

    public List<Statement> getThenStmts() {return this.then_stmts;}
    public List<Statement> getElseStmts() {return this.else_stmts;}

    public boolean returnExprTerminated(Type returnType, Map<String, FunctionDecl> funcMap,
                                         Map<String, Type> varAndParamMap) throws SemanticAnalysisException{
        if (else_stmts != null) {

            int sizeofthen = then_stmts.size();
            int sizeofelse = else_stmts.size();

            Statement stmtThen = then_stmts.get(0);
            Statement stmtElse = else_stmts.get(0);

            boolean baseCaseFlag = (stmtThen instanceof ExprReturn_Statement) && (stmtElse instanceof ExprReturn_Statement);

            // Case 1
            if (sizeofthen == 1 && sizeofelse == 1 && baseCaseFlag){
//                System.out.println("Case 1");
                Expression exprThen = ((ExprReturn_Statement) stmtThen).getExpr();
                Expression exprElse = ((ExprReturn_Statement) stmtElse).getExpr();

                Type exprThenType = exprThen.analyzeAndGetType(funcMap, varAndParamMap);
                Type exprElseType = exprElse.analyzeAndGetType(funcMap, varAndParamMap);

                boolean flag = returnType.toString().equals(exprElseType.toString()) && returnType.toString().equals(exprThenType.toString());
                // if both return expressions are expected return types --> return true
//                System.out.println(flag);
                return flag;
                // Case 2
            }  else if (sizeofthen == 1 && (stmtThen instanceof ExprReturn_Statement)) {
//                System.out.println("Case 2");
                Expression exprThen = ((ExprReturn_Statement) stmtThen).getExpr();
                Type exprThenType = exprThen.analyzeAndGetType(funcMap, varAndParamMap);

                boolean isElseStmtsTerminated = false;

                for (Statement stmt: else_stmts){
                    if (stmt instanceof IfThenElse_Statement){
                        IfThenElse_Statement castedStmt = (IfThenElse_Statement) stmt;
                        isElseStmtsTerminated = castedStmt.returnExprTerminated(returnType, funcMap, varAndParamMap);
                    }

                }

                return returnType.toString().equals(exprThenType.toString()) && isElseStmtsTerminated;
                // Case 3
            } else if (sizeofelse == 1 && (stmtElse instanceof ExprReturn_Statement)) {
//                System.out.println("Case 3");
                Expression exprElse = ((ExprReturn_Statement) stmtElse).getExpr();
                Type exprElseType = exprElse.analyzeAndGetType(funcMap, varAndParamMap);

                boolean isThenStmtsTerminated = false;

                for (Statement stmt: then_stmts){

                    if (stmt instanceof IfThenElse_Statement){
                        IfThenElse_Statement castedStmt = (IfThenElse_Statement) stmt;
                        isThenStmtsTerminated = castedStmt.returnExprTerminated(returnType, funcMap, varAndParamMap);
                    }
                }

                return returnType.toString().equals(exprElseType.toString()) && isThenStmtsTerminated;
                // Case 4
            } else {
//                System.out.println("Case 4");
                boolean isThenStmtsTerminated = false;
                boolean isElseStmtsTerminated = false;

                for (Statement stmt: then_stmts){

                    if (stmt instanceof IfThenElse_Statement){
                        IfThenElse_Statement castedStmt = (IfThenElse_Statement) stmt;
                        isThenStmtsTerminated = castedStmt.returnExprTerminated(returnType, funcMap, varAndParamMap);
                    }
                }

                for (Statement stmt: else_stmts){
                    if (stmt instanceof IfThenElse_Statement){
                        IfThenElse_Statement castedStmt = (IfThenElse_Statement) stmt;
                        isElseStmtsTerminated = castedStmt.returnExprTerminated(returnType, funcMap, varAndParamMap);
                    }
                }

                return isThenStmtsTerminated && isElseStmtsTerminated;
            }


        }

        return false;
    }


    @Override
    public void analyze(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap) throws SemanticAnalysisException {
        Type exprType = this.expr.analyzeAndGetType(funcMap, varAndParamMap);

        if (!exprType.toString().equals("Boolean")) {
            throw new SemanticAnalysisException("Expression for While loop should be Boolean", expr.getLine(), expr.getColumn());
        }
        // Check other statements
        for (Statement stmt: this.then_stmts) {
            stmt.analyze(funcMap, varAndParamMap);
        }

        // if there are else stmts and not null
        if (else_stmts != null) {
            for (Statement stmt: this.else_stmts) {
                stmt.analyze(funcMap, varAndParamMap);
            }
        }

    }

    @Override
    public void execute(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ReturnFromCall, ExecutionException {

        boolValue flag = (boolValue) expr.evaluate(funcMap, varAndParamMap);
        // If Then Else stmt
        if (else_stmts != null) {

            if (flag.convValue()){
                for (Statement stmt : then_stmts) {
                    stmt.execute(funcMap, varAndParamMap);
                }
            } else {
                for (Statement stmt : else_stmts) {
                    stmt.execute(funcMap, varAndParamMap);
                }
            }

        } else { // If Then stmt
            if (flag.convValue()) {
                for (Statement stmt : then_stmts) {
                    stmt.execute(funcMap, varAndParamMap);
                }
            }
        }


    }
}

package splat.parser.elements;
import splat.executor.*;
import splat.lexer.Token;
import splat.semanticanalyzer.SemanticAnalysisException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import splat.parser.elements.*;

public class FunctionCall_Expression extends Expression{

    private Label label;
    private Args args;
    private String text;

    public FunctionCall_Expression(Token tok, Label label, Args args) {
        super(tok);
        this.label = label;
        this.args = args;
        this.text = label + " ( " + args + " ) ";
    }

    public String toString() {return this.text;}

    // Comparison of function call expression with collected data about the function outside
    @Override
    public Type analyzeAndGetType(Map<String, FunctionDecl> funcMap, Map<String, Type> varAndParamMap)
            throws SemanticAnalysisException {

        // Check for function existence (given in expression label) in the funcMap
        String funcExpr = this.label.toString();

        if (!funcMap.containsKey(funcExpr)) {
            throw new SemanticAnalysisException("The function is not defined", this.label.getLine(), this.label.getColumn());
        }

        // Check for the argument and parameter numbers
        int arg_size = this.args.getExprs().size();
        int param_size = funcMap.get(funcExpr).getParams().size();

        if (arg_size != param_size) {
            throw new SemanticAnalysisException("Mismatch in the number of arguments and parameters", this.args.getLine(), this.args.getColumn());
        }

        // Check for parameter and argument types , size is already checked !
        List<Param> paramList = funcMap.get(funcExpr).getParams();

        for (int ind = 0; ind < param_size; ind++){
            Expression expr = this.args.getExprs().get(ind);
            Type argType = expr.analyzeAndGetType(funcMap, varAndParamMap);
            Type paramType = paramList.get(ind).getType();

            if (!argType.toString().equals(paramType.toString())){
                throw new SemanticAnalysisException("Argument and parameter types are mismatched", expr.getLine(), expr.getColumn());
            }
        }


        // if everything is okay return its type
        return funcMap.get(funcExpr).getRetType();
    }

    @Override
    public Value evaluate(Map<String, FunctionDecl> funcMap, Map<String, Value> varAndParamMap) throws ExecutionException {

        Map<String, Value> VarAndParamMap = new HashMap<>();
        FunctionDecl funcDecl = funcMap.get(label.toString());
        // Map <var_name, value>
        for (Declaration decl: funcDecl.getLocVarDecls()){

            VariableDecl var_decl = (VariableDecl)decl;
            Label label = var_decl.getLabel();
            Type varType = var_decl.getType();
            Value val;
            // initialize Value with default values (0, false, "")
            if (varType.toString().equals("Integer")) {val = new intValue(varType);}
            else if (varType.toString().equals("String")) {val = new strValue(varType);}
            else {val = new boolValue(varType);}

            VarAndParamMap.put(label.toString(), val);
        }

        List<Value> arg_list= new ArrayList<>(); //Collecting values for args

        for (Expression expr: this.args.getExprs()){
            Value arg_value = expr.evaluate(funcMap, varAndParamMap);
            arg_list.add(arg_value);
        }

        for (int i=0; i<funcDecl.getParams().size(); i++){ // Adding params to Map
            Param param = funcDecl.getParams().get(i);
            Value value = arg_list.get(i);
            Label label = param.getLabel();
            Type type = param.getType();
            Value val;

            if (value instanceof intValue valueCasted) {
                val = new intValue(type, valueCasted.convValue());
            }
            else if (value instanceof strValue valueCasted) {
                val = new strValue(type, valueCasted.getValue());
            }
            else {
                boolValue valueCasted = (boolValue)value;
                val = new boolValue(type, valueCasted.convValue());
            }

            VarAndParamMap.put(label.toString(), val);
        }

        List<Statement> funcStmts = funcMap.get(label.toString()).getStmts();

        try {
            // Go through and execute each of the statements
            // We assume that return <expr> will throw ReturnFromCall somewhere during execution of stmts
            // Exception should be thrown during stmts execution else return null
            for (Statement stmt : funcStmts) {
                stmt.execute(funcMap, VarAndParamMap);
            }

        } catch (ReturnFromCall ex) {

            //System.out.println(label.toString() + " function returned value (expr): " +  ex.getReturnVal().getValue());
            return ex.getReturnVal();
        }

        return null;
    }
}

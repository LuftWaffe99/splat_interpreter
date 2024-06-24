package splat.parser.elements;

import splat.lexer.Token;
import java.util.Map;
import splat.semanticanalyzer.SemanticAnalysisException;
import splat.executor.*;

public abstract class Expression extends ASTElement {

	public Expression(Token tok) {
		super(tok);
	}

	/**
	 * This will be needed for Phase 3 - this abstract method will need to be
	 * implemented by every Expression subclass.  This method does two things:
	 * 
	 * 1) Performs typechecking and semantic analysis on this expression, and
	 * recursively calls the same method on any sub-expressions.  Note that we
	 * will usually need the types of the immediate sub-expressions to make 
	 * sure all the parts of this expression are of proper types.
	 * 
	 * 2) Determines the type of this expression. 
	 * 
	 * funcMap is needed in case this expression or a sub-expression contains
	 * a function call -- we would need to make sure the argument number and 
	 * types match, and also get the return type.
	 * 
	 * varAndParamMap is needed in case this expression or a sub-expression
	 * contains variables or parameters -- we use this map to keep track of
	 * what items are currently in scope, and what their types are
	 */
	 public abstract Type analyzeAndGetType(Map<String, FunctionDecl> funcMap,
	                                        Map<String, Type> varAndParamMap) throws SemanticAnalysisException;
	
	/**
	 * This will be needed for Phase 4 - this abstract method will need to be
	 * implemented by every Expression subclass.  This method is used to 
	 * "calculate" the value of this expression, which will usually require we
	 * recursively call the same method on all sub-expressions. 
	 * 
	 * funcMap is needed in case this expression or a sub-expression contains
	 * a function call -- we will have to evaluate the individual arguments and 
	 * create a new varAndParamMap to bind the function params to the new values
	 * and then execute the function body.  More on this later...
	 *  
	 * varAndParamMap is needed in case this expression or a sub-expression
	 * contains variables or parameters -- we use this map to keep track of the
	 * values of the items that are currently in scope
	 */
	public abstract Value evaluate(Map<String, FunctionDecl> funcMap,
                                 Map<String, Value> varAndParamMap) throws ExecutionException;
}

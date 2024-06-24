package splat.executor;

import splat.SplatException;
import splat.parser.elements.ASTElement;

public class ExecutionException extends SplatException {

	public ExecutionException(String msg, ASTElement elem) {
		super(msg, elem.getLine(), elem.getColumn());
	}
	
	public ExecutionException(String msg, int line, int column) {
		super(msg, line, column);
	}
}

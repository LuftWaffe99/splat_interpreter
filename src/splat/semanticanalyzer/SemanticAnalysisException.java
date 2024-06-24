package splat.semanticanalyzer;

import splat.SplatException;
import splat.parser.elements.ASTElement;

public class SemanticAnalysisException extends SplatException {
	
	public SemanticAnalysisException(String msg, ASTElement elem) {
		super(msg, elem.getLine(), elem.getColumn());
	}
	
	public SemanticAnalysisException(String msg, int line, int column) {
		super(msg, line, column);
	}
}

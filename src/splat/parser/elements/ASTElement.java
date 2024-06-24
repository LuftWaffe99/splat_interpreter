package splat.parser.elements;

import splat.lexer.Token;

public abstract class ASTElement {

	private int line;
	private int column;
	
	public ASTElement(Token tok) {
		this.line = tok.getLine();
		this.column = tok.getColumn();
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
}

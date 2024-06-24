package splat.parser.elements;

import splat.lexer.Token;

public class VariableDecl extends Declaration {

	private Type type;

	public VariableDecl(Token tok, Label label, Type type) {
		super(tok, label);
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "VariableDecl{" +
				"label=" + getLabel() +
				", type=" + type +
				'}';
	}
}






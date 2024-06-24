package splat.parser.elements;

import splat.lexer.Token;

public abstract class Declaration extends ASTElement {

	private Label label;
	public Declaration(Token tok, Label label) {

		super(tok);
		this.label = label;
	}

	public Label getLabel() {
		return label;
	}
}

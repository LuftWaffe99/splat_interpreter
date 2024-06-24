package splat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import splat.lexer.Lexer;
import splat.lexer.Token;
import splat.parser.Parser;
import splat.parser.elements.ProgramAST;
import splat.semanticanalyzer.*;
import splat.executor.*;

public class Splat {

	private File progFile;
	
	public Splat(File progFile) {
		this.progFile = progFile;
	}
	
	public void processFileAndExecute() throws SplatException, IOException {
		
		// Step 1.  Tokenize
		Lexer lexer = new Lexer(progFile);
		List<Token> tokens = lexer.tokenize();


		 //Step 2.  Parse
		 Parser parser = new Parser(tokens);
		 ProgramAST progAST = parser.parse();

		// Step 3.  Semantic Analysis
		 SemanticAnalyzer analyzer = new SemanticAnalyzer(progAST);
		 analyzer.analyze();

		// Step 4.  Executor
		 Executor executor = new Executor(progAST);
		 executor.runProgram();
		
		// THE END!
	}

//	public static void main(String args[]) throws Exception {
//		File file = new File("tests/777_01_goodexecution.splat");
//		Splat splat = new Splat(file);
//		splat.processFileAndExecute();
//	}
}


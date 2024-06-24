package splat;

import java.io.*;

public class  SplatTester {

	// Set this to false if you don't want to see detials about
	// individual test cases
	private final boolean verbose = true;

	// Path to where the directory of .splat test files are located
	private final String testDirName = "./tests";

	private File testDir;

	private int totalTests;

	private int totalTestsRun;

	private int[] successCount;
	private int[] resCodeCount;
	private int[] falseThrows;

	public static void main(String[] args) throws Exception {

		SplatTester tester = new SplatTester();
		tester.runTests();
	}

	public SplatTester() {
		totalTests = 0;
		successCount = new int[]{0, 0, 0, 0, 0};
		resCodeCount = new int[]{0, 0, 0, 0, 0};
		falseThrows = new int[]{0, 0, 0, 0};
	}

	public void runTests() throws Exception {

		// First, we get the testing files

		testDir = new File(testDirName);

		System.out.print("Opening test directory...");

		if (!testDir.exists() || !testDir.isDirectory()) {
			System.out.println("error!");
			System.out.println("Cannot find directory 'tests'.");
			System.out.println("Please create one in your project folder, and add"
					+ " the appropriate testing files.");
			return;
		}

		System.out.println("success");

		File[] testFiles = testDir.listFiles((dir, name) -> name.endsWith(".splat"));

		totalTests = testFiles.length;
		System.out.println("Number of tests found: " + totalTests);

		// Now, we run the tests

		System.out.println("Running tests...");

		// For each .splat test file...
		for (File testFile : testFiles) {

			// Update the result coude count
			int expectedResultCode = getExpectedResultCode(testFile.getName());
			resCodeCount[expectedResultCode]++;

			// Run the actual test
			runTest(testFile);
		}

		// Count up the total passes and get the percentage
		int totalSuccesses = 0;
		for (int i = 0; i < 5; i++) {
				totalSuccesses += successCount[i];
		}
		double percentPass = 100.0 * totalSuccesses / totalTests;

		// Finally, we output the results

		System.out.println("---------------------------");
		System.out.println("FINAL SPLAT TESTING RESULTS");
		System.out.println("---------------------------");
		System.out.println("Total tests cases:   " + totalTests);
		System.out.println("Test cases run:      " + totalTestsRun);
		System.out.println("Test cases passed:   " + totalSuccesses + " (" +
				String.format("%.1f", percentPass) + " %)");
		System.out.println("Results by case");
		System.out.println("  Lex Exception:       " + scoreString(0));
		System.out.println("    false throws: " + falseThrows[0]);
		System.out.println("  Parse Exception:     " + scoreString(1));
		System.out.println("    false throws: " + falseThrows[1]);
		System.out.println("  Semantic Exception:  " + scoreString(2));
		System.out.println("    false throws: " + falseThrows[2]);
		System.out.println("  Execution Exception: " + scoreString(3));
		System.out.println("    false throws: " + falseThrows[3]);
		System.out.println("  Execution Success:   " + scoreString(4));
	}

	private String scoreString(int resCode) {
		double percent = 100.0 * successCount[resCode] / resCodeCount[resCode];
		return successCount[resCode] + " / " + resCodeCount[resCode] + " ("
				+ String.format("%.1f", percent) + " %)";
	}

	private int getExpectedResultCode(String filename) throws Exception {

		if (filename.endsWith("badlex.splat")) {
			return 0;
		} else if (filename.endsWith("badparse.splat")){
			return 1;
		} else if (filename.endsWith("badsemantics.splat")){
			return 2;
		} else if (filename.endsWith("badexecution.splat")){
			return 3;
		} else if (filename.endsWith("goodexecution.splat")){
			return 4;
		}

		throw new Exception("Bad .splat test filename");
	}

	private int getActualResultCode(SplatException ex) throws Exception {

		String exType = ex.getClass().getName();

		if (exType.equals("splat.lexer.LexException")) {
			return 0;
		} else if (exType.equals("splat.parser.ParseException")) {
			return 1;
		} else if (exType.equals("splat.semanticanalyzer.SemanticAnalysisException")) {
			return 2;
		} else if (exType.equals("splat.executor.ExecutionException")) {
			return 3;
		}

		throw new Exception("Non-splat exception thrown");
	}

	private void runTest(File testFile) throws Exception {

		totalTestsRun++;
		System.out.print("Test Case " + totalTestsRun + ": " + testFile.getName() + "...");

		Splat splat = new Splat(testFile);

		// The expected result code is determined by the .splat filename
		int expectedResultCode = getExpectedResultCode(testFile.getName());
		int actualResultCode;

		// Used to show exception messages in verbase mode
		String execptMsg = "";

		// Redirect the program output to a file, instead of the console window
		PrintStream originalOut = new PrintStream(System.out);
		File progOutput = new File(testDir, "temp-out.txt");
		PrintStream outs = new PrintStream(progOutput);
		System.setOut(outs);

		try {
			// Run the analyzer on the program AST
			splat.processFileAndExecute();

			// Successfully executed
			actualResultCode = 4;

		} catch (SplatException ex) {

			int ind = ex.getClass().getName().lastIndexOf('.');
			execptMsg = " >>> " + ex.getClass().getName().substring(ind + 1) + ": " + ex.toString();

			// Get the error code from the exception thrown
			actualResultCode = getActualResultCode(ex);

		} catch (Exception ex) {

			int ind = ex.getClass().getName().lastIndexOf('.');
			execptMsg = " >>> " + ex.getClass().getName().substring(ind + 1) + ": " + ex.toString();

			// Means that the test case failed due to an unexpected non-splat exception
			actualResultCode = -1;

		} finally {

			// Restore System.out to the console, no matter what happens
			outs.close();
			System.setOut(originalOut);
		}

		// For the phase in which an exception is expected to be thrown, we check that
		// it has been thrown in that exact phase
		if (expectedResultCode < 4) {

			if (expectedResultCode == actualResultCode) {
				System.out.println("passed (proper SplatException thrown during Phase "+ (expectedResultCode+1) + ")");
				successCount[expectedResultCode]++;
			} else {
				System.out.println("failed (exception was expected to be thrown during Phase "+ (expectedResultCode+1) + ")");
				if (actualResultCode < 4 && actualResultCode != -1) {
					falseThrows[actualResultCode]++;
				}
			}
		}

		// In the case that execution did not successfully complete when it should have, output
		// a failure message
		if (expectedResultCode == 4 && actualResultCode != 4) {
			System.out.println("failed (exception was thrown when execution should have been successful)");
			if (actualResultCode != -1) {
				falseThrows[actualResultCode]++;
			}
		}

		// Output any exception messages produced
		if (verbose && execptMsg.length() > 0) {
			System.out.println(execptMsg);
		}

		// In the case that execution successfully completed, we need to verify
		// that the actual output matches what was expected in this case
		if (expectedResultCode == 4 && actualResultCode == 4) {

			// Get the .txt file with the expected output
			String testFilePath = testFile.getAbsolutePath();
			String exFilename = testFilePath.substring(0, testFilePath.length() - 5) + "out";
			File expectedOutput = new File(exFilename);

			// Check if the output was as expected
			if (outputMatchesExpected(progOutput, expectedOutput)) {
				System.out.println("passed (output matches expected results)");
				successCount[4]++;
			} else {
				System.out.println("failed (output does not match expected results)");
			}

			if (verbose) {
				printOutput(progOutput);
				System.out.println();
			}
		}

	}

	private boolean outputMatchesExpected(File output, File expected) throws IOException {

		if (!expected.exists()) {
			System.out.println("File " + expected.getAbsolutePath() + " not found");
			return false;
		}

		BufferedReader readerOut = new BufferedReader(new FileReader(output));
		BufferedReader readerEx = new BufferedReader(new FileReader(expected));

		boolean result = true;

		int chOut = readerOut.read();
		int chEx = readerEx.read();

		while (true) {

			while (chOut == '\r') {
				chOut = readerOut.read();
			}
			while (chEx == '\r') {
				chEx = readerEx.read();
			}

			if (chOut == -1 && chEx == -1) {

				result = true;
				break;

			} else if (chOut != chEx) {
				result = false;
				break;
			}

			chOut = readerOut.read();
			chEx = readerEx.read();
		}

		readerOut.close();
		readerEx.close();

		return result;
	}

	private void printOutput(File file) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(file));

		int ch = reader.read();

		while (ch != -1) {
			System.out.print((char)ch);
			ch = reader.read();
		}

		reader.close();

	}

}

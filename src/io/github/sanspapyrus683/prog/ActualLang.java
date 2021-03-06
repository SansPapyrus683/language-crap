package io.github.sanspapyrus683.prog;

import io.github.sanspapyrus683.prog.errors.RuntimeError;
import io.github.sanspapyrus683.prog.inner.Interpreter;
import io.github.sanspapyrus683.prog.inner.Stmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ActualLang {
    private static boolean errored = false;
    private static boolean runtimeErrored = false;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("my man i need a file");
        } else {
            runFile(args[0]);
        }
    }

    /** reads & runs the file (details of running in the run method) */
    private static void runFile(String path) throws IOException {
        BufferedReader read = new BufferedReader(new FileReader(path));
        // the entire file in a single stringbuilder
        StringBuilder res = new StringBuilder();
        String line;
        // read all the lines from the file
        while ((line = read.readLine()) != null) {
            res.append(line).append('\n');
        }
        run(res.toString());
        if (errored || runtimeErrored) {
            System.exit(1);
        }
    }

    /** parses & interprets the code, also does the handling i guess */
    private static void run(String code) {
        Tokenizer sc = new Tokenizer(code);
        List<Token> tokens = sc.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> expr = parser.parse();
        // don't even try to execute the code if there was a parse error
        if (errored) {
            return;
        }
        new Interpreter().interpret(expr);
    }

    /** report an error at a line in general or at a specific token */
    public static void error(int line, String msg) {
        report(line, "", msg);
    }

    public static void error(Token token, String msg) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", msg);
        } else {
            report(token.line, " at '" + token.lexeme + "'", msg);
        }
    }

    private static void report(int line, String where, String msg) {
        System.err.println("[line " + line + "] Error" + where + ": " + msg);
        errored = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + " (line " + error.token.line + ")");
        runtimeErrored = true;
    }
}

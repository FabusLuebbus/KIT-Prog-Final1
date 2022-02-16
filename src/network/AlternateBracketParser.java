package src.network;

import src.exceptions.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class AlternateBracketParser {
    public static final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
            + "(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3})$";
    public static final String IP_PATTERN_WITH_WHITESPACE = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
            + "(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3}) $";
    private String lookahead;
    private StreamTokenizer tokenizer;

    private void next() throws IOException {
        tokenizer.nextToken();
        lookahead = tokenizer.sval;
    }

    private void match(String expected) throws IOException, ParseException {
        if (!lookahead.equals(expected)) {
            throw new ParseException("unexpected token");
        } else {
            next();
        }
    }

    private void parse(String bracketNotation) throws IOException, ParseException {         //X
        if (bracketNotation.contains("  ")) {
            throw new ParseException("Double whitespaces are not allowed");
        }
        String editedBracketNotation = bracketNotation.replace("(", "( ").replace(")", " )");
        Reader reader = new StringReader(editedBracketNotation);
        tokenizer = new StreamTokenizer(reader);
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.wordChars('(', '9');

        match("(");
        parseBracketContent();
        match(")");
    }

    private void parseBracketContent() throws IOException, ParseException {     //Y
        //TODO match IP and whitespace
        parseSecondaryBracketContent();
    }


    private void parseSecondaryBracketContent() throws IOException, ParseException {        //Y'
        if (lookahead == StreamTokenizer.TT_WORD) {
            next();
            next();
            parseIP();
        } else if (lookahead == '(') {
            next();
            next();
            parseBracketContent();
        }
        if (lookahead == ')') {
            throw new ParseException("Missing closing bracket at end");
        }
        parseIP();
    }

    private void parseIP() throws IOException, ParseException {

    }
    
}

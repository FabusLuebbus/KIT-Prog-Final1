package src.parsing;

import src.exceptions.ParseException;
import src.ip.IP;
import src.exceptions.ErrorMessages;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author usmsk
 * @version 3.0
 */
public class BracketNotationParser {

    /**
     * regex to check input tokens supposed to be IPs for syntax
     */
    public static final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
                + "(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3})$";

    private String lookahead;
    private StreamTokenizer tokenizer;
    private int nestingDepth = 0;
    private final List<IP> roots = new LinkedList<>();
    private final List<IP> nodes = new LinkedList<>();
    private final Set<String> addedIPs = new HashSet<>();
    private boolean firstTime = true;

    /**
     * getter method for product of parsing bracketNotation
     *
     * @return Set of nodes
     */
    public List<IP> getNodes() {
        return nodes;
    }

    private void next() throws IOException {
        tokenizer.nextToken();
        lookahead = tokenizer.sval;
    }

    private void match(String expected) throws IOException, ParseException {
        if (lookahead == null || !lookahead.equals(expected)) {
            throw new ParseException(ErrorMessages.EXPECTED_BRACKET);
        } else {
            next();
        }
    }

    private void matchIP() throws ParseException, IOException {
        //checking for invalid IP-syntax / double entries
        if (lookahead == null || !lookahead.matches(IP_PATTERN) || addedIPs.contains(lookahead)) {
            throw new ParseException(ErrorMessages.IP_MATCHING_ERROR);
        } else {
            //creating new node from IP and adding it to nodes, adding its production root as adjacent node if existent
            IP node = new IP(lookahead);
            if (roots.size() > nestingDepth) {
                node.addAdjacentNode(roots.get(nestingDepth));
            }
            addedIPs.add(lookahead);
            nodes.add(node);
        }
        next();
    }

    /**
         * initial method to be called when parsing a bracket notation.
         * Does some syntax checks only possible on string form of input, then uses {@link StreamTokenizer} to split input
         * into tokens.
         * then begins parsing descent by matching '(' and calling parseBracketContent after recursion ends two conditions
         * have to be met in order to have successfully parsed the inputString:
         * - there must be a ')' left to match
         * - after ')' the current token must be TT_EOF(token type indicating end of input stream is reached)
         *
         * @param bracketNotation input string containing supposed network topology
         * @throws IOException    when a {@link StreamTokenizer} action fails
         * @throws ParseException when syntax is broken
         */
    public void parse(String bracketNotation) throws IOException, ParseException {
        /*
        checking for multiple whitespace since they are to be removed when tokenizing string
        which makes such checks impossible.
         */
        if (bracketNotation.matches(".* {2}.*|.*\\( .*|.* \\).*|^$") || ' ' == (bracketNotation.charAt(0))
                || ' ' == (bracketNotation.charAt(bracketNotation.length() - 1))) {
            throw new ParseException(ErrorMessages.WHITESPACE_ERROR);
        }
        //replacing '(' with '( ' and ')' with ' )' so brackets will be independent tokens as well as IPs
        String editedBracketNotation = bracketNotation.replace("(", "( ").replace(")", " )");
        //setting up Streamtokenizer and its syntax
        Reader reader = new StringReader(editedBracketNotation);
        tokenizer = new StreamTokenizer(reader);
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.wordChars('(', '9');
        next();
        //starting parsing
        match("(");
        matchIP();
        IP currentRoot = nodes.get(nodes.size() - 1);
        roots.add(currentRoot);
        parseBracketContent();
        match(")");
        //finished parsing. now checking if input stream also ended. If yes input was successfully parsed.
        if (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            throw new ParseException(ErrorMessages.WHITESPACE_ERROR);
        }
    }

    /*
        following methods simply represent the base grammar's productions
         */
    private void parseBracketContent() throws IOException, ParseException {
        if (lookahead != null && lookahead.equals("(")) {
            next();
            matchIP();
            IP currentRoot = nodes.get(nodes.size() - 1);
            roots.add(currentRoot);
            nestingDepth++;
            parseBracketContent();
            match(")");
        } else {
            matchIP();
        }
        if (lookahead != null && !lookahead.equals(")")) {
            parseBracketContent();
        } else {
            nestingDepth--;
            roots.remove(roots.size() - 1);
        }
    }
}



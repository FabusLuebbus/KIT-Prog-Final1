package src.parsing;

import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.*;

/**
 * Top-down parser used to parse String containing network in network topology. While parsing BracketNotationParsers
 * check for syntactic validity and gather a list of nodes with every node pointing towards its direct production root
 * via adjacentNodes Set.
 *
 * following grammar was base for the parser though it was minimally changed to fix minor inaccuracies:
 *      - nonterminals = {S, BRACKETCONTENT(BC), SECONDARYBRACKETCONTENT(SBC), IP, SECONDARYIP(SIP)}
 *      - terminals = { '(' , 'ip' , ')' }, starting symbol = 'S'
 *      - productions = {
 *          (S -> (BC)), (BC -> ipIP | ip(BC)IP), (IP -> ipIP | ip(BC)IP | ε)
 *      }
 *
 * @author usmsk
 * @version 1.0
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
        if (!lookahead.equals(expected)) {
            throw new ParseException("unexpected token");
        } else {
            next();
        }
    }

    private void matchIP() throws ParseException {
        //checking for invalid IP-syntax / double entries
        if (!lookahead.matches(IP_PATTERN) || addedIPs.contains(lookahead)) {
            throw new ParseException("Invalid bracket notation (check number of IPs, brackets, "
                    + "Ip-syntax and look for double entries)");
        } else {
            //creating new node from IP and adding it to nodes, adding its production root as adjacent node if existent
            IP node = new IP(lookahead);
            if (roots.size() > nestingDepth) {
                node.addAdjacentNode(roots.get(nestingDepth));
            }
            addedIPs.add(lookahead);
            nodes.add(node);
        }
    }

    /**
     * initial method to be called when parsing a bracket notation.
     * Does some syntax checks only possible on string form of input, then uses {@link StreamTokenizer} to split input
     * into tokens.
     * then begins parsing descent by matching '(' and calling parseBracketContent after recursion ends two conditions
     * have to be met in order to have successfully parsed the inputString:
     *      - there must be a ')' left to match
     *      - after ')' the current token must be TT_EOF(token type indicating end of input stream is reached)
     *
     * @param bracketNotation input string containing supposed network topology
     * @throws IOException when a {@link StreamTokenizer} action fails
     * @throws ParseException when syntax is broken
     */
    public void parse(String bracketNotation) throws IOException, ParseException {
        /*initial checks on string with 17 being minimal length for valid bracket notations ((X.X.X.X X.X.X.X) -> 7+7+3)
        checking for multiple whitespace since they are to be removed when tokenizing string
        which makes such checks impossible.
         */
        if (bracketNotation.matches(".* {2}.*|.*\\( .*|.* \\).*") || bracketNotation.length() < 17) {
            throw new ParseException("This is not a valid bracket notation");
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
        parseBracketContent();
        match(")");
        //finished parsing. now checking if input stream also ended. If yes input was successfully parsed.
        if (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            throw new ParseException("Invalid bracket Notation");
        }
    }

    /*
    following methods simply represent the base grammar's productions
     */
    private void parseBracketContent() throws IOException, ParseException {
        matchIP();
        if (firstTime) {
            firstTime = false;
        } else {
            nestingDepth++;
        }
        IP currentRoot = nodes.get(nodes.size() - 1);
        roots.add(currentRoot);
        next();
        if (lookahead.equals("(")) {
            next();
            parseBracketContent();
            match(")");
            roots.remove(roots.size() - 1);
            nestingDepth--;
        }
        parseIP();
    }

    private void parseIP() throws IOException, ParseException {
        if (lookahead != null && !lookahead.equals(")")) {
            matchIP();
            next();
        }
        if (lookahead != null && lookahead.equals("(")) {
            next();
            parseBracketContent();
            match(")");
            roots.remove(roots.size() - 1);
            nestingDepth--;
        }
        if (lookahead != null && !lookahead.equals(")")) {
            parseIP();
        }
    }
}
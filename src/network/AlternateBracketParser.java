package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * can return list of nodes extracted from bracketNotation all nodes ONLY contain their direct root as adjacentNode
 */
public class AlternateBracketParser {
    public static final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
            + "(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3})$";

    private String lookahead;
    private StreamTokenizer tokenizer;
    private int nestingDepth = 0;
    private List<IP> roots = new ArrayList<>();
    private List<IP> nodes = new ArrayList<>();
    private IP currentRoot;
    private boolean firstTime = true;

    public Set<IP> getNodes() {
        return Set.copyOf(nodes);
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

    private void matchIP() throws IOException, ParseException {
        if (!lookahead.matches(IP_PATTERN)) {
            throw new ParseException("Invalid Bracketnotation (check number of IPs, brackets and Ip-syntax)");
        } else {
            IP node = new IP(lookahead);
            if (roots.size() > nestingDepth) {
                node.addAdjacentNode(roots.get(nestingDepth));
            }
            nodes.add(node);
        }
    }



    public boolean parse(String bracketNotation) throws IOException, ParseException {
        if (bracketNotation.matches(".*\\*.*|.*\\+.*|.*,.*|.*-.*|.*/.*|.* {2}.*") || bracketNotation.length() < 17) {
            throw new ParseException("This is not a valid bracket notation");
        }
        String editedBracketNotation = bracketNotation.replace("(", "( ").replace(")", " )");
        Reader reader = new StringReader(editedBracketNotation);
        tokenizer = new StreamTokenizer(reader);
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.wordChars('(', '9');
        next();

        match("(");
        parseBracketContent();
        match(")");

        return true;
    }

    private void parseBracketContent() throws IOException, ParseException {
        matchIP();
        if (firstTime) {
            firstTime = false;
        } else {
            nestingDepth++;
        }
        currentRoot = nodes.get(nodes.size() - 1);
        roots.add(currentRoot);
        next();
        parseSecondaryBracketContent();
    }


    private void parseSecondaryBracketContent() throws IOException, ParseException {
        if (lookahead.equals("(")) {
            next();
            parseBracketContent();
            match(")");
            roots.remove(nestingDepth);
            nestingDepth--;
        } else {
            matchIP();
            next();
        }
        if (lookahead == null) {
            throw new ParseException("Wrong syntax, might check your brackets");
        }
        parseIP();
    }

    private void parseIP() throws IOException, ParseException {
        if (lookahead != null && !lookahead.equals(")")) {
            matchIP();
            next();
            parseSecondaryIP();
        }
        if (lookahead == null) {
            throw new ParseException("Wrong syntax, might check your brackets");
        }
    }

    private void parseSecondaryIP() throws IOException, ParseException {
        if (lookahead.matches(IP_PATTERN)) {
            parseIP();
        }

        if (lookahead.equals("(")) {
            parseSecondaryBracketContent();
        }
        if (lookahead == null) {
            throw new ParseException("Wrong syntax, might check your brackets");
        }
    }
}

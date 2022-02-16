package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.sql.Array;
import java.util.*;

public class BracketNotationParser {
    public static final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
            + "(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3})$";
    private Set<IP> nodes = new LinkedHashSet<IP>();
    private Set<Edge> edges = new LinkedHashSet<Edge>();

    public void parse(String bracketNotation) throws ParseException {
        //TODO remove thist code piece and solve bracketcounting diffeerently
        //find indices of brackets and save in two lists
        //finding '('
        List<Integer> indicesOfOpenBrackets = new ArrayList<Integer>();
        int indexForOpenBracket = 0;
        while (indexForOpenBracket >= 0) {
            indicesOfOpenBrackets.add(indexForOpenBracket);
            indexForOpenBracket = bracketNotation.indexOf('(', indexForOpenBracket + 1);
        }
        //finding ')'
        List<Integer> indicesOfClosingBrackets = new ArrayList<Integer>();
        int indexForClosingBracket = bracketNotation.indexOf(')');
        while (indexForClosingBracket >= 0) {
            indicesOfClosingBrackets.add(indexForClosingBracket);
            indexForClosingBracket = bracketNotation.indexOf(')', indexForClosingBracket + 1);
        }


        //checking for legal bracket expression
        if (indicesOfOpenBrackets.size() != indicesOfClosingBrackets.size()) {
            throw new ParseException("This is no valid bracket notation! Check numbers of opening / closing brackets");
        }

        //splitting input and calculating levels of nodes
        bracketNotation = bracketNotation.replace("(", "(#");
        bracketNotation = bracketNotation.replace(")", "#)");
        String[] inputAsArray = bracketNotation.split("#|\\s");
        List<String> inputAsList = List.of(inputAsArray);

        List<IP> roots = new ArrayList<IP>();
        //checking for roots
        for (int i = 0; i < inputAsList.size() ; i++) {
            if (inputAsList.get(i).equals("(") && !inputAsList.get(i + 1).equals("(")) {
                IP newRoot = new IP(inputAsList.get(i + 1));
                roots.add(newRoot);
                nodes.add(newRoot);
            } else if (inputAsList.get(i).matches(IP_PATTERN)) {
                nodes.add(new IP(inputAsList.get(i)));
            }
        }
        //adding all adjacent nodes to roots and checking numbers of brackets
        int nestingDepth = 0;
        for (IP root : roots) {
            nestingDepth = 0;
            int startindex = inputAsList.indexOf(root.toString());
            List<String> partialInputList = inputAsList.subList(startindex, inputAsList.size());
            for (int i = 1; i < partialInputList.size(); i++) {
                if (partialInputList.get(i).equals("(")) {
                    nestingDepth++;
                    root.addAdjacentNode(new IP(partialInputList.get(i + 1)));
                    i = partialInputList.indexOf(")") - 1;
                } else if (partialInputList.get(i).equals(")")) {
                    nestingDepth--;
                    partialInputList = partialInputList.subList(i + 1, partialInputList.size());
                    i = -1;
                    if (nestingDepth < 0) {
                        break;
                    }
                } else {
                    root.addAdjacentNode((new IP(partialInputList.get(i))));
                }
            }
        }
        //generating edges from adjacencyList
        for (IP root : roots) {
            for (IP adjacentNode : root.getAdjacentNodes()) {
                edges.add(new Edge(root, adjacentNode));
            }
        }

    }


    public Set<IP> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }
}

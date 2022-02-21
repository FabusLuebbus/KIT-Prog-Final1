package src.printing;

import src.ip.IP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * class which provides functionality to print a given network topology (given as list of levels)
 * as a String in bracket notation
 *
 * @author usmsk
 * @version 1.0
 */
public class BracketNotationPrinter {
    private final List<Object> parts = new ArrayList<>();
    private final StringBuilder output = new StringBuilder();

    /**
     * iterates over parts and produces output string. If print was already called parts contains all IPs and brackets
     * in correct order.
     *
     * @return String bracketNotation
     */
    public String getBracketNotation() {
        output.append('(');
        for (Object part : parts) {
            output.append(part).append(' ');
        }
        output.append(')');
        return output.toString().replace("( ", "(").replace(" )", ")");
    }

    /**
     * initial method to start printing network
     *
     * @param levels input of network as list of levels
     */
    public void print(List<List<IP>> levels) {
        List<IP> upperTree = levels.get(0);
        parts.add(upperTree.get(0));
        List<IP> adjNodes = new ArrayList<>(upperTree.get(0).getAdjacentNodes());
        Collections.sort(adjNodes);
        for (IP adj : adjNodes) {
            printAdjacentNodes(adj);
        }
        addBrackets();
    }

    private void printAdjacentNodes(IP root) {
        if (root.getAdjacentNodes().size() == 1) {
            parts.add(root);
        } else {
            List<IP> adjNodes = new ArrayList<>(root.getAdjacentNodes());
            Collections.sort(adjNodes);
            parts.add(root);
            for (IP adj : adjNodes) {
                if (!adj.equals(root.getParent())) {
                    printAdjacentNodes(adj);
                }
            }
        }
    }

    private void addBrackets() {
        boolean ignore = true;
        boolean putClosingBracket = false;
        int numberOfBracketsToClose = 0;
        int numberOfOpenBrackets = 0;
        int offset = 0;
        for (int i = 0; i < parts.size(); i++) {
            IP current = (IP) parts.get(i + offset);

            if (putClosingBracket) {
                for (int j = 0; j < numberOfBracketsToClose; j++) {
                    parts.add(i + offset, ")");
                    numberOfOpenBrackets--;
                    offset++;
                }
                putClosingBracket = false;
            }
            if (i + 1 + offset >= parts.size()) {
                break;
            }
            IP next = (IP) parts.get(i + 1 + offset);
            if (current.getLevel() > next.getLevel()) {
                putClosingBracket = true;
                numberOfBracketsToClose = current.getLevel() - next.getLevel();
            }
            if (ignore) {
                ignore = false;
            } else {
                if (current.getLevel() < next.getLevel()) {
                    parts.add(i + offset, "(");
                    numberOfOpenBrackets++;
                    ignore = true;
                }
            }
        }
        for (int k = 0; k < numberOfOpenBrackets; k++) {
            parts.add(")");
        }
    }
}

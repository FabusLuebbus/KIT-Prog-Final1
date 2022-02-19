package src.printing;

import src.ip.IP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BracketNotationPrinter {
    private List<IP> upperTree;
    private List<Object> output = new ArrayList<>();
    private StringBuilder s = new StringBuilder();
    public String getBracketNotation() {
        s.append('(');
        for (Object part : output) {
            s.append(part).append(' ');
        }
        s.append(')');
        return s.toString().replace("( ", "(").replace(" )", ")");
    }

    public void print(List<List<IP>> levels) {
        upperTree = levels.get(0);
        output.add(upperTree.get(0));
        List<IP> adjNodes = new ArrayList<>(upperTree.get(0).getAdjacentNodes());
        Collections.sort(adjNodes);
        for (IP adj : adjNodes) {
            printAdjacentNodes(adj);
        }
        addBrackets();
        //upperTree.remove(0);
    }
//(S -> (BC)), (BC -> ipIP | ip(BC)IP), (IP -> ipIP | ip(BC)IP | ε)
    private void printAdjacentNodes(IP root) {
        if (root.getAdjacentNodes().size() == 1) {
            output.add(root);
        } else {
            List<IP> adjNodes = new ArrayList<>(root.getAdjacentNodes());
            Collections.sort(adjNodes);
            output.add(root);
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
        for (int i = 0; i < output.size() - 1; i++) {
            IP current = (IP) output.get(i + offset);
            if (i + 1 + offset >= output.size()) {
                break;
            }
            IP next = (IP) output.get(i + 1 + offset);
            if (putClosingBracket) {
                for (int j = 0; j < numberOfBracketsToClose; j++) {
                    output.add(i, ")");
                    numberOfOpenBrackets--;
                    offset++;
                }
                putClosingBracket = false;
            }
            if (current.getLevel() > next.getLevel()) {
                putClosingBracket = true;
                numberOfBracketsToClose = current.getLevel() - next.getLevel();
            }
            if (ignore) {
                ignore = false;
            } else {
                if (current.getLevel() < next.getLevel()) {
                    output.add(i + offset, "(");
                    numberOfOpenBrackets++;
                    ignore = true;
                }
            }
        }
        for (int k = 0; k < numberOfOpenBrackets; k++) {
            output.add(")");
        }
    }
}

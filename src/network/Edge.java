package src.network;

import src.ip.IP;

/**
 * class to interpret to adjacent nodes as one edge
 *
 * @author usmsk
 * @version 1.0
 */
public class Edge {
    private final IP firstNode;
    private final IP secondNode;

    /**
     * constructor creating edge from to given nodes (IPs)
     *
     * @param firstNode first IP
     * @param secondNode second IP
     */
    public Edge(IP firstNode, IP secondNode) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
    }

    /**
     * getter method for first node
     *
     * @return first node (IP)
     */
    public IP getFirstNode() {
        return firstNode;
    }

    /**
     * getter method for second node
     *
     * @return second node (IP)
     */
    public IP getSecondNode() {
        return secondNode;
    }

    @Override
    public String toString() {
        return "(" + firstNode.toString() + ", " + secondNode.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge)) {
            return false;
        }

        return (firstNode.equals(((Edge) o).firstNode) && secondNode.equals(((Edge) o).secondNode)
                || (firstNode.equals(((Edge) o).secondNode) && secondNode.equals(((Edge) o).firstNode)));
    }

    @Override
    public int hashCode() {
        return 42;
    }
}

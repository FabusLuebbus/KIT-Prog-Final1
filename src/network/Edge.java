package src.network;

import src.ip.IP;

public class Edge {
    private IP firstNode;
    private IP secondNode;

    public Edge(IP firstNode, IP secondNode) {
        this.firstNode = firstNode;
        this.secondNode = secondNode;
    }

    public IP getFirstNode() {
        return firstNode;
    }

    public IP getSecondNode() {
        return secondNode;
    }

    @Override
    public String toString() {
        return "(" + firstNode.toString() + ", " + secondNode.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != Edge.class) {
            return false;
        }

        return (firstNode.equals(((Edge) o).firstNode) && secondNode.equals(((Edge) o).secondNode) || (firstNode.equals(((Edge) o).secondNode) && secondNode.equals(((Edge) o).firstNode)));
    }

    @Override
    public int hashCode() {
        return 7;
    }
}

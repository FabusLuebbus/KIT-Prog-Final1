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
}

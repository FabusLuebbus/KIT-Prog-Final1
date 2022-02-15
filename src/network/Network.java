package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.util.*;

public class Network {
    /*hashmap for nodes while boolean variable indicates 'visited'
    private final Map<Node, Boolean> nodes = new HashMap<Node, Boolean>();

     */
    private final Set<IP> nodes = new LinkedHashSet<IP>();
    private final Set<Edge> edges = new LinkedHashSet<Edge>();
    public Network(final IP root, final List<IP> children) throws ParseException { //alarm wegen änderung der methodensignatur
        if (root == null || children == null) { throw new IllegalArgumentException("argument not instantiated."); }
        List<IP> childrenCopy = new LinkedList<>();
        for (IP child : children) {
            if (child == null) { throw new IllegalArgumentException("child is not instantiated."); }
            childrenCopy.add(new IP(child.toString()));
        }

        nodes.addAll(childrenCopy);
        IP[] nodesArray = nodes.toArray(new IP[0]);
        for (IP node : nodesArray) {
            edges.add(new Edge(root, node));
        }
        nodes.add(root);
        //TODO add call to isValidTree Method once implemented
    }

    public Network(final String bracketNotation) throws ParseException {
    }

    public Set<IP> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public boolean add(final Network subnet) {
        return false;
    }

    public List<IP> list() {
        return null;
    }

    public boolean connect(final IP ip1, final IP ip2) {
        return false;
    }

    public boolean disconnect(final IP ip1, final IP ip2) {
        return false;
    }

    public boolean contains(final IP ip) {
        return false;
    }

    public int getHeight(final IP root) {
        return 0;
    }

    public List<List<IP>> getLevels(final IP root) {
        return null;
    }

    public List<IP> getRoute(final IP start, final IP end) {
        return null;
    }

    public String toString(IP root) {
        return null;
    }
}
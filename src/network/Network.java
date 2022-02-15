package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.util.*;

public class Network {
    /*hashmap for nodes while key is ip in String representation and value contains reference to ip itself
    */
    private  Set<IP> nodes = new LinkedHashSet<IP>();
    private  Set<Edge> edges = new LinkedHashSet<Edge>(); //TODO check if edges set is needed

    public Network(final IP root, final List<IP> children) throws ParseException { //alarm wegen änderung der methodensignatur

        if (root == null || children == null) { throw new IllegalArgumentException("argument not instantiated."); }

        List<IP> childrenCopy = new LinkedList<>();
        for (IP child : children) {
            if (child == null) { throw new IllegalArgumentException("child is not instantiated."); }
            childrenCopy.add(new IP(child.toString()));
        }
        //ready to start
        Iterator<IP> iterator = childrenCopy.iterator();
        while (iterator.hasNext()) {
            IP ip = iterator.next();
            nodes.add(ip);
        }
        //now all children are properly inserted

        //adding edges TODO check if edges set is needed
        IP[] nodesArray = nodes.toArray(new IP[0]);
        for (IP node : nodesArray) {
            edges.add(new Edge(root, node));
        }
        nodes.add(root);
        //TODO add call to isValidTree Method once implemented
    }

    public Network(final String bracketNotation) throws ParseException {
        BracketNotationParser parser = new BracketNotationParser();
        parser.parse(bracketNotation);
        nodes = parser.getNodes();
        edges = parser.getEdges();
    }
    //TODO think about reusing first constructor in 2nd constructor


    //TODO following 2 methods are only used in tests!
    public Set<IP> getNodes() {
        return nodes; //TODO think about immutable map here
    }

    public Set<Edge> getEdges() {
        //TODO check if adding would destroy tree

        //TODO add nodes and edges
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
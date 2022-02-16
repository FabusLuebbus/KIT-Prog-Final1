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
            ip.addAdjacentNode(root);
        }
        //now all children are properly inserted

        //adding edges TODO check if edges set is needed
        IP[] nodesArray = nodes.toArray(new IP[0]);
        for (IP node : nodesArray) {
            edges.add(new Edge(root, node));
        }
        nodes.add(root);
        root.addAdjacentNodeCollection(childrenCopy);
        //TODO add call to isValidTree Method once implemented
    }

    public Network(final String bracketNotation) throws ParseException {
        BracketNotationParser parser = new BracketNotationParser();
        parser.parse(bracketNotation);
        nodes = parser.getNodes();
        edges = parser.getEdges();
        for (Edge edge : edges) {
            IP node1 = edge.getFirstNode();
            IP node2 = edge.getSecondNode();
            if (nodes.contains(node1)) {
                node1.addAdjacentNode(node2);
            }
            if (nodes.contains(node2)) {
                node2.addAdjacentNode(node1);
            }
        }
    }
    //TODO think about reusing first constructor in 2nd constructor


    //TODO following 2 methods are only used in tests!
    public Set<IP> getNodes() {
        return nodes; //TODO think about immutable map here
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public boolean add(final Network subnet) {
        //creating temporary Lists (not Sets, but using sets for distinctiveness) of both networks combined to check for legality
        nodes.addAll(subnet.nodes);
        edges.addAll(subnet.edges);
        for (Edge edge : edges) {
            IP node1 = edge.getFirstNode();
            IP node2 = edge.getSecondNode();
            if (nodes.contains(node1)) {            //maybe one of those if statements is enough
                node1.addAdjacentNode(node2);
            }
            if (nodes.contains(node2)) {
                node2.addAdjacentNode(node1);
            }
        }
        List<IP> union = List.copyOf(nodes);
        if (!treeIsValid(union)) {
            //TODO check if adding would destroy tree

            return false;
        }



        //add nodes and edges, duplicates are avoided automatically because of add implementation in set


        return true;
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

    public boolean treeIsValid(List<IP> nodes) {
        //implement bfs
        /*  during bfs check for closed paths and if there are unvisited nodes after bfs. Check each of them
            by using bfs again
         */
        /*  criteria for valid trees:
            - at least 2 connected nodes
            - no closed paths / only distinct paths
         */

        //implement queue
        Queue<IP> queue = new LinkedList<>();
        Set<IP> visitedNodes = new HashSet<>();
        //choosing root and setting up
        IP root = nodes.get(0);
        queue.add(root);
        root.setVisited(true);
        visitedNodes.add(root);

        while (!queue.isEmpty()) {
            IP current = queue.poll();
            //check for isolated nodes
            if (current.getAdjacentNodes().isEmpty()) {
                return false;
            }
            //breadth first search looking for reachability
            for (IP adjacentNode : current.getAdjacentNodes()) {
                if (!adjacentNode.getVisited() && adjacentNode != current.getParent()) {
                    adjacentNode.setVisited(true);
                    visitedNodes.add(adjacentNode);
                    adjacentNode.setParent(current);
                    queue.add(adjacentNode);
                } else if (adjacentNode.getVisited() && adjacentNode != current.getParent()) {
                    return false; //checking for double paths / cycles
                }

            }
        }
        //checking if all nodes were reached if not start recursion at left nodes
        if (visitedNodes.containsAll(nodes)) {
            return true;
        }
        //transfer all missing nodes into one list
        List<IP> notVisited = new LinkedList<>();
        for (IP node : nodes) {
            if (!visitedNodes.contains(node)) {
                notVisited.add(node);
            }
        }
        //check list of not visited nodes for integrity using recursion
        return treeIsValid(notVisited);
    }
}
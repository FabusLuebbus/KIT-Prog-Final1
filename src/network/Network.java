package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.util.*;

public class Network {
    /*hashmap for nodes while key is ip in String representation and value contains reference to ip itself
    */
    private  Set<IP> nodes = new LinkedHashSet<IP>();
    private  Set<Edge> edges = new LinkedHashSet<Edge>();

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

    //TODO avoid double nodes maybe in parser
    public Network(final String bracketNotation) throws ParseException {
        AlternateBracketParser parser = new AlternateBracketParser();
        try {
            parser.parse(bracketNotation);
        } catch (IOException e) {
            throw new ParseException("Not a valid bracket notation");
        }
        nodes = parser.getNodes();
        for (IP node : nodes) {
            for (IP adjacentNode : node.getAdjacentNodes()) {
                adjacentNode.addAdjacentNode(node);
                edges.add(new Edge(adjacentNode, node));
            }
        }
    }



    //TODO following 2 methods are only used in tests!
    public Set<IP> getNodes() {
        return nodes; //TODO think about immutable map here
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public boolean add(final Network subnet) throws ParseException {
        //creating temporary Lists (not Sets, but using sets for distinctiveness) of both networks combined to check for legality
         List<IP> tempNodesList = new ArrayList<>();
        for (IP node : nodes) {
             //TODO catch exception
            if (!tempNodesList.contains(node)) {
                IP nodeClone = new IP(node.toString());
                tempNodesList.add(nodeClone);
            }
            for (IP adjacentNode : node.getAdjacentNodes()) {
                if (tempNodesList.contains(adjacentNode)) {
                    tempNodesList.get(tempNodesList.indexOf(adjacentNode)).addAdjacentNode(tempNodesList.get(tempNodesList.indexOf(node)));
                } else {
                    IP adjacentNodeClone = new IP(adjacentNode.toString());
                    adjacentNodeClone.addAdjacentNode(tempNodesList.get(tempNodesList.indexOf(node)));
                    tempNodesList.get(tempNodesList.indexOf(node)).addAdjacentNode(adjacentNodeClone);
                    tempNodesList.add(adjacentNodeClone);
                }
            }

        }

        for (IP node : subnet.nodes) {
            if (tempNodesList.contains(node)) {
                for (IP adjacentNode : node.getAdjacentNodes()) {
                    if (tempNodesList.contains(adjacentNode)) {
                        tempNodesList.get(tempNodesList.indexOf(node)).addAdjacentNode(tempNodesList.get(tempNodesList.indexOf(adjacentNode)));
                    } else {
                        tempNodesList.get(tempNodesList.indexOf(node)).addAdjacentNode(adjacentNode);
                    }
                }
            } else {
                for (IP adjacentNode : node.getAdjacentNodes()) {
                    adjacentNode.addAdjacentNode(node);
                    tempNodesList.add(node);
                }

            }

        }
        /*for (Edge edge : edges) {
            IP node1 = edge.getFirstNode();
            IP node2 = edge.getSecondNode();
            if (nodes.contains(node1) && nodes.contains(node2)) {        //maybe one of those if condition is redundant
                node1.addAdjacentNode(node2);
                node2.addAdjacentNode(node1);
            }

        }

         */
        List<IP> union = List.copyOf(tempNodesList);
        if (!treeIsValid(union)) {
            //TODO check if adding would destroy tree

            return false;
        }



        //add nodes and edges, duplicates are avoided automatically because of add implementation in set
        nodes = new HashSet<>(tempNodesList);
        edges.addAll(subnet.edges);
        List<IP> list = new ArrayList<>(nodes);
        return treeIsValid(list);
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
        //TODO use marker object in root for other uses
        //implement bfs
        /*  during bfs check for closed paths and if there are unvisited nodes after bfs. Check each of them
            by using bfs again
         */
        /*  criteria for valid trees:
            - at least 2 connected nodes
            - no closed paths / only distinct paths
         */

        for (IP node : nodes) {
            node.setParent(null);
            node.setVisited(false);
        }
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
                if (!adjacentNode.getVisited()) {
                    adjacentNode.setVisited(true);
                    visitedNodes.add(adjacentNode);
                    adjacentNode.setParent(current);
                    queue.add(adjacentNode);
                } else if (adjacentNode.getVisited() && !adjacentNode.equals(current.getParent())) {
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
package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.util.*;

public class Network {
    private  Set<IP> nodes = new LinkedHashSet<>();
    private  Set<Edge> edges = new LinkedHashSet<>();

    public Network(final IP root, final List<IP> children) {

        if (root == null || children == null) { throw new IllegalArgumentException("argument not instantiated."); }

        //generating mutable deep copy of children
        List<IP> childrenCopy = new LinkedList<>();
        try {
            for (IP child : children) {
                if (child == null || child.equals(root)) {
                    throw new IllegalArgumentException("child is not instantiated or equals root");
                }
                childrenCopy.add(new IP(child.toString()));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid IPs");
        }

        //adding all children to nodes with root as their only adjacent node, if child is already added throw exception
        for (IP child : childrenCopy) {
            if (nodes.contains(child)) {
                throw new IllegalArgumentException("Duplicate entry in child list");
            }
            nodes.add(child);
            child.addAdjacentNode(root);
        }
        //adding edges
        for (IP child : childrenCopy) {
            edges.add(new Edge(root, child));
        }
        //add root last so iterations over nodes make sense
        nodes.add(root);
        root.addAdjacentNodeCollection(childrenCopy);
        //check if tree is valid
        if (!treeIsValid(List.copyOf(nodes))) {
            throw new IllegalArgumentException("Not a valid tree topology");
        }
    }
    
    public Network(final String bracketNotation) throws ParseException {
        //trying to parse bracketNotation
        BracketNotationParser parser = new BracketNotationParser();
        try {
            parser.parse(bracketNotation);
        } catch (IOException e) {
            throw new ParseException("Not a valid bracket notation");
        }
        /*
        parsed successfully. since parser returns nodes only pointing at their 'production root' we have to complete
        all adjacentNodes lists. (adding edges in the process)
         */
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

        //creating temporary nodes list to simulate adding of subnet
        //TODO if used again extract to copyNodes()
        List<IP> tempNodesList = new ArrayList<>();
        for (IP node : nodes) {
            try {

                if (!tempNodesList.contains(node)) {
                    IP nodeClone = new IP(node.toString());
                    tempNodesList.add(nodeClone);
                }
                //making sure to reference clone in List
                IP nodeCloneInList = getIPFromList(node, tempNodesList);
                //iterating over original node's adjacency list to provide copy as adjacency list to clones
                for (IP adjacentNode : node.getAdjacentNodes()) {
                    if (tempNodesList.contains(adjacentNode)) {
                        getIPFromList(adjacentNode, tempNodesList).addAdjacentNode(nodeCloneInList);
                    } else {
                        IP adjClone = new IP(adjacentNode.toString());
                        adjClone.addAdjacentNode(nodeCloneInList);
                        nodeCloneInList.addAdjacentNode(adjClone);
                        tempNodesList.add(adjClone);
                    }
                }

            } catch (ParseException e) {
                return false;
            }
        }
        //current state: created temporary testing list of nodes
        //following: add subnet nodes
        for (IP subNode : subnet.nodes) {
            if (tempNodesList.contains(subNode)) {
                for (IP subAdjNode : subNode.getAdjacentNodes()) {
                    if (tempNodesList.contains(subAdjNode)) {
                        //subNode and aubAdjNode are both contained in tempNodesList
                        getIPFromList(subNode, tempNodesList).addAdjacentNode(getIPFromList(subAdjNode, tempNodesList));
                        getIPFromList(subAdjNode, tempNodesList).addAdjacentNode(getIPFromList(subNode, tempNodesList));
                    } else {
                        //subNode is contained in tempNodesList but subAdjNode not
                        getIPFromList(subNode, tempNodesList).addAdjacentNode(subAdjNode);
                        //make subAdjNode point to correct node (in List)
                        subAdjNode.getAdjacentNodes().remove(subNode);
                        subAdjNode.addAdjacentNode(getIPFromList(subNode, tempNodesList));
                        tempNodesList.add(subAdjNode);
                    }
                }
            } else {
                for (IP subAdjNode : subNode.getAdjacentNodes()) {
                    if (tempNodesList.contains(subAdjNode)) {
                        //subNode is not contained in tempNodeList but subAdjNode is
                        getIPFromList(subAdjNode, tempNodesList).addAdjacentNode(subNode);
                        //make subNode point to correct node (in List)
                        subNode.getAdjacentNodes().remove(subAdjNode);
                        subNode.addAdjacentNode(getIPFromList(subAdjNode, tempNodesList));
                        tempNodesList.add(subNode);
                    } else {
                        //neither subNode nor subAdjNode are contained tempNodesList
                        //assumption: no need to add connection since they are connected already
                        tempNodesList.add(subNode);
                        tempNodesList.add(subAdjNode);
                    }

                }

            }

        }
        //check if adding would destroy tree
        if (!treeIsValid(tempNodesList)) {
            return false;
        }



        //add nodes and edges, duplicates are avoided automatically because of add implementation in set
        nodes = new HashSet<>(tempNodesList);
        //clear edges and generate new edges based on new nodes
        edges.clear();
        for (IP node : nodes) {
            for (IP adjNode : node.getAdjacentNodes()) {
                edges.add(new Edge(node, adjNode));
            }
        }
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
        //TODO put in bfs class
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

    private static IP getIPFromList(IP ip, List<IP> list) {
        return list.get(list.indexOf(ip));
    }
}
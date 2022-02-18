package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.util.*;

public class Network {
    private  Set<IP> nodes = new LinkedHashSet<>();
    private  Set<Edge> edges = new LinkedHashSet<>();

    /**
     * generates a network of height 1. links all children to root and the other way round
     *
     * @param root is the node (IP) all children are adjacent to
     * @param children is a List of nodes (IPs) containing all nodes directly adjacent to root
     */
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
        if (!networkIsValid(List.copyOf(nodes))) {
            throw new IllegalArgumentException("Not a valid tree topology");
        }
    }

    /**
     * creates new Network from bracket notation. Uses {@link BracketNotationParser} to parse input.
     * Then retrieves nodes from parser and creates edges accordingly
     *
     *
     * @param bracketNotation input string containing structure of new network
     * @throws ParseException if parser fails to parse input
     */
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

    /**
     * Tries to add new subnet to this Network. Checks validity of new Network using {@link #networkIsValid(List)}.
     * If new network is not valid no changes are made on this Network
     *
     * @param subnet the network to be added to this network
     * @return  whether the network was succesfully changed
     */
    public boolean add(final Network subnet) {
        //creating temporary nodes list to simulate adding of subnet
        List<IP> tempNodesList;
        try {
            tempNodesList = deepCopy(nodes);
        } catch (ParseException | IllegalArgumentException e) {
            return false;
        }
        // create deep copy list of subnet.nodes
        List<IP> subnetNodes;
        try {
            subnetNodes = deepCopy(subnet.nodes);
        } catch (ParseException | IllegalArgumentException e) {
            return false;
        }
        //current state: created temporary testing list of nodes
        //following: add subnet nodes
        for (IP subNode : subnetNodes) {
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
        if (!networkIsValid(tempNodesList)) {
            return false;
        }
        //add nodes and edges, duplicates are avoided automatically because of add implementation in set
        nodes = new LinkedHashSet<>(tempNodesList);
        //clear edges and generate new edges based on new nodes
        edges.clear();
        for (IP node : nodes) {
            for (IP adjNode : node.getAdjacentNodes()) {
                edges.add(new Edge(node, adjNode));
            }
        }
        return true;
    }

    /**
     * returns sorted list of nodes using Collections.sort() which sorts the list in relation to the node's IP-values
     *
     * @return sorted nodes list
     */
    public List<IP> list() {
        List<IP> sortedNodes = new ArrayList<>(nodes);
        Collections.sort(sortedNodes);
        return sortedNodes;
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

    //TODO use marker object in root for other uses

    /**
     *  checks a network for validity. A network is considered invalid if one of the following states appears:
     *          - one of the nodes has no adjacent node
     *          - there are cyclic routes / 2 different routes from one node to another
     *
     *  The network is evaluated using an implementation of breadth first search following these steps:
     *          - set all node's parent to null and visited parameter to false
     *          - find random node to begin with (root) and set visited to true
     *       -> - add all adjacent nodes to queue and set their parent to current node / visited to true
     * loop |   - add all adjacent nodes to seperate visitedNodes Set
     *       -- - get new current node from queue
     *
     *  the last 3 steps are repeated until queue is empty or:
     *          - one node has 0 adjacent nodes (return false)
     *          - if one of current node's adjacent nodes is visited but not current node's parent (return false)
     *          
     *  if the algorithm was not interrupted and queue is empty visitedNodes Set is compared to nodes List.
     *  If all nodes were visited return true, else create List of missing nodes and
     *  call networkIsValid on missing nodes.
     *  Repeat recursion until return true
     *
     * @param nodes List of nodes to be evaluated
     * @return whether nodes represents a valid network
     */
    public boolean networkIsValid(List<IP> nodes) {
        //reset parameters of nodes
        for (IP node : nodes) {
            node.setParent(null);
            node.setVisited(false);
        }
        //implement queue
        Queue<IP> queue = new LinkedList<>();
        Set<IP> visitedNodes = new HashSet<>();
        //getting random root and setting up
        IP root = nodes.get(0);
        queue.add(root);
        root.setVisited(true);
        visitedNodes.add(root);
        //main loop
        while (!queue.isEmpty()) {
            IP current = queue.poll();
            //check for isolated nodes
            if (current.getAdjacentNodes().isEmpty()) {
                return false;
            }
            //mark all adjacent nodes and add them to queue
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
        //checking if all nodes were reached if not start recursion at missing nodes
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
        return networkIsValid(notVisited);
    }

    private static IP getIPFromList(IP ip, List<IP> list) {
        return list.get(list.indexOf(ip));
    }

    private static List<IP> deepCopy(Collection<IP> nodes) throws ParseException {
        List<IP> nodesCopy = new LinkedList<>();
        for (IP node : nodes) {
            if (!nodesCopy.contains(node)) {
                IP nodeClone = new IP(node.toString());
                nodesCopy.add(nodeClone);
            }
            //making sure to reference clone in List
            IP nodeCloneInList = getIPFromList(node, nodesCopy);
            //iterating over original node's adjacency list to provide copy as adjacency list to clones
            for (IP adjacentNode : node.getAdjacentNodes()) {
                if (nodesCopy.contains(adjacentNode)) {
                    getIPFromList(adjacentNode, nodesCopy).addAdjacentNode(nodeCloneInList);
                } else {
                    IP adjClone = new IP(adjacentNode.toString());
                    adjClone.addAdjacentNode(nodeCloneInList);
                    nodeCloneInList.addAdjacentNode(adjClone);
                    nodesCopy.add(adjClone);
                }
            }
        }
        return nodesCopy;
    }
}
package src.network;

import src.exceptions.ParseException;
import src.ip.IP;
import src.parsing.BracketNotationParser;
import src.printing.BracketNotationPrinter;

import java.io.IOException;
import java.util.*;

public class Network {
    //TODO probably turn nodes into List and remove lIst.copyOf usages
    private  List<IP> nodes = new LinkedList<>();

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
        BracketNotationParser parser = new BracketNotationParser(); //TODO remove alternate from name once sure its working
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
            }
        }
    }

    //TODO following 2 methods are only used in tests!
    public List<IP> getNodes() {
        return nodes; //TODO think about immutable map here
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

        tempNodesList = deepCopy(nodes);
        // create deep copy list of subnet.nodes
        List<IP> subnetNodes;

        subnetNodes = deepCopy(subnet.nodes);

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
                tempNodesList.add(subNode);
                for (IP subAdjNode : subNode.getAdjacentNodes()) {
                    if (tempNodesList.contains(subAdjNode)) {
                        //subNode is not contained in tempNodeList but subAdjNode is
                        getIPFromList(subAdjNode, tempNodesList).addAdjacentNode(subNode);
                        //make subNode point to correct node (in List)
                        subNode.getAdjacentNodes().remove(subAdjNode);
                        subNode.addAdjacentNode(getIPFromList(subAdjNode, tempNodesList));
                    } else {
                        //neither subNode nor subAdjNode are contained tempNodesList
                        //assumption: no need to add connection since they are connected already
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
        nodes = tempNodesList;
        //clear edges and generate new edges based on new nodes
        return true;
    }

    /**
     * returns sorted list of nodes using Collections.sort() which sorts the list in relation to the node's IP-values
     *
     * @return sorted nodes list
     */
    public List<IP> list() {
        List<IP> sortedNodes = deepCopy(nodes);
        Collections.sort(sortedNodes);
        return sortedNodes;
    }

    public boolean connect(final IP ip1, final IP ip2) {
        if (ip1 != null && ip2 != null && !ip1.equals(ip2) && nodes.contains(ip1) && nodes.contains(ip2)) {
            List<IP> currentNodes = new ArrayList<>(nodes);
            getIPFromList(ip1, currentNodes).addAdjacentNode(getIPFromList(ip2, currentNodes));
            getIPFromList(ip2, currentNodes).addAdjacentNode(getIPFromList(ip1, currentNodes));
            if (networkIsValid(currentNodes)) {
                return true;
            }
            getIPFromList(ip1, currentNodes).getAdjacentNodes().remove(getIPFromList(ip2, currentNodes));
            getIPFromList(ip2, currentNodes).getAdjacentNodes().remove(getIPFromList(ip1, currentNodes));
        }
        return false;
    }

    public boolean disconnect(final IP ip1, final IP ip2) {
        List<IP> currentNodes = new ArrayList<>(nodes);
        Set<IP> ip1AdjNodes = getIPFromList(ip1, currentNodes).getAdjacentNodes();
        Set<IP> ip2AdjNodes = getIPFromList(ip2, currentNodes).getAdjacentNodes();

        if (ip1 == null || ip1.equals(ip2) || !nodes.contains(ip1) || !nodes.contains(ip2)
                || (ip1AdjNodes.size() == 1 && ip2AdjNodes.size() == 1) ) {
            return false;
        }
        ip1AdjNodes.remove(getIPFromList(ip2, currentNodes));
        ip2AdjNodes.remove(getIPFromList(ip1, currentNodes));
        if (ip1AdjNodes.size() == 0) {
            nodes.remove(getIPFromList(ip1, currentNodes));
        }
        if (ip2AdjNodes.size() == 0) {
            nodes.remove(getIPFromList(ip2, currentNodes));
        }
        return true;
    }

    public boolean contains(final IP ip) {
        for (IP node : nodes) {
            node.setVisited(false);
        }
        //implement queue
        Queue<IP> queue = new LinkedList<>();
        //getting random root and setting up
        IP root = List.copyOf(nodes).get(0);
        queue.add(root);
        root.setVisited(true);
        //main loop
        while (!queue.isEmpty()) {
            IP current = queue.poll();
            //mark all adjacent nodes and add them to queue
            for (IP adjacentNode : current.getAdjacentNodes()) {
                if (adjacentNode.equals(ip)) {
                    return true;
                }
                if (!adjacentNode.getVisited()) {
                    adjacentNode.setVisited(true);
                    queue.add(adjacentNode);
                }
            }
        }
        return false;
    }

    public int getHeight(final IP root) {
        for (IP node : nodes) {
            node.setParent(null);
        }
        List<IP> currentNodes = new ArrayList<>(nodes);
        IP currentRoot = getIPFromList(root, currentNodes);
        return heightRecursion(currentRoot) - 1;
    }

    private static int heightRecursion(final IP root) {
        int currentHeight = 0;
        for (IP adjacentNode : root.getAdjacentNodes()) {
            if (adjacentNode.getParent() == null) {
                adjacentNode.setParent(root);
            }
            if (!adjacentNode.equals(root.getParent())) {

                currentHeight = Math.max(currentHeight, heightRecursion(adjacentNode));
            }
        }
        return ++currentHeight;
    }

    public List<List<IP>> getLevels(final IP root) {
        List<List<IP>> output = getLevelsUnsorted(root);
        for (List<IP> level : output) {
            Collections.sort(level);
        }
        return output;
    }

    private List<List<IP>> getLevelsUnsorted(IP root) {
        List<List<IP>> output = new LinkedList<>();
        if (!nodes.contains(root)) {
            return output;
        }
        List<IP> currentNodes;

        currentNodes = deepCopy(nodes);

        int currLevel = 0;
        output.add(new LinkedList<IP>());
        //start
        for (IP node : currentNodes) {
            node.setParent(null);
            node.setVisited(false);
            //node.setLevel(0);
        }
        //implement queue
        Queue<Object> queue = new LinkedList<>();
        String levelMarker = "";
        //getting random root and setting up
        queue.add(getIPFromList(root, currentNodes));
        queue.add(levelMarker);
        getIPFromList(root, currentNodes).setVisited(true);
        IP current;
        //main loop
        while (!queue.isEmpty()) {
            if (queue.peek().equals(levelMarker)) {
                currLevel++;
                output.add(new LinkedList<>());
                queue.poll();
            }
            if (queue.peek() != null && queue.peek().equals(levelMarker)) {
                output.remove(output.size() - 1);
                break;
            }
            current = (IP) queue.poll();
            output.get(currLevel).add(current);
            //mark all adjacent nodes and add them to queue
            for (IP adjacentNode : current.getAdjacentNodes()) {
                if (!adjacentNode.getVisited() && !adjacentNode.equals(current.getParent())) {
                    adjacentNode.setVisited(true);
                    adjacentNode.setParent(current);
                    adjacentNode.setLevel(currLevel + 1);
                    queue.add(adjacentNode);
                }
            }
            if (queue.peek().equals(levelMarker)) {
                queue.add(levelMarker);
            }
        }
        return output;
    }

    public List<IP> getRoute(final IP start, final IP end) {
        for (IP node : nodes) {
            node.setParent(null);
            node.setVisited(false);
        }
        //implement queue
        Queue<IP> queue = new LinkedList<>();
        List<IP> output = new LinkedList<>();
        IP root = getIPFromList(start, List.copyOf(nodes));
        queue.add(root);
        root.setVisited(true);
        IP current = root;
        //main loop
        while (!queue.isEmpty()) {
            current = queue.poll();
            //mark all adjacent nodes and add them to queue
            for (IP adjacentNode : current.getAdjacentNodes()) {
                if (adjacentNode.equals(end)) {
                    adjacentNode.setParent(current);
                    current = adjacentNode;
                    queue.clear();
                    break;
                }
                if (!adjacentNode.getVisited() && !adjacentNode.equals(current.getParent())) {
                    adjacentNode.setParent(current);
                    adjacentNode.setVisited(true);
                    queue.add(adjacentNode);
                }
            }
        }
        if (!current.equals(end)) {
            return new LinkedList<>();
        }
        //current state we found end. And by using parent attributes path is already found
        output.add(0, current);
        while (!current.equals(start)) {
            output.add(0, current.getParent());
            current = current.getParent();
        }
        return deepCopy(output);
    }

    public String toString(IP root) {
        IP thisRoot = getIPFromList(root, List.copyOf(nodes));
        BracketNotationPrinter printer = new BracketNotationPrinter();
        printer.print(getLevelsUnsorted(thisRoot));
        return printer.getBracketNotation();
    }


    

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
    private boolean networkIsValid(List<IP> nodes) {
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

    private static List<IP> deepCopy(Collection<IP> nodes) {
        List<IP> nodesCopy = new LinkedList<>();
        for (IP node : nodes) {
            IP nodeClone;
            try {
                nodeClone = new IP(node.toString());
            } catch (ParseException e) {
                return new ArrayList<>();
            }
            nodesCopy.add(nodeClone);
        }

        for (IP node : nodes) {
            if (nodesCopy.contains(node)) {
                //making sure to reference clone in List
                IP nodeClone = getIPFromList(node, nodesCopy);
                IP adjNodeClone;
                //iterating over original node's adjacency list to provide copy as adjacency list to clones
                for (IP adjacentNode : node.getAdjacentNodes()) {
                    if (nodesCopy.contains(adjacentNode)) {
                        adjNodeClone = getIPFromList(adjacentNode, nodesCopy);
                        nodeClone.addAdjacentNode(adjNodeClone);
                        adjNodeClone.addAdjacentNode(nodeClone);
                    }
                }
            }
        }
        return nodesCopy;
    }

    //TODO use same find method in contains and get route
}
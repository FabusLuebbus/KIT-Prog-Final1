package src.network;

import src.exceptions.ErrorMessages;
import src.exceptions.ParseException;
import src.ip.IP;
import src.parsing.BracketNotationParser;
import src.printing.BracketNotationPrinter;
import static src.network.NetworkUtil.getIPFromList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Network {
    private List<IP> nodes = new LinkedList<>();

    /**
     * generates a network of height 1. links all children to root and the other way round
     *
     * @param root is the node (IP) all children are adjacent to
     * @param children is a List of nodes (IPs) containing all nodes directly adjacent to root
     */
    public Network(final IP root, final List<IP> children) {

        if (root == null || children == null) { throw new IllegalArgumentException("argument not instantiated."); }

        //generating mutable deep copy of children
        List<IP> childrenCopy = NetworkUtil.deepCopy(children);
        if (childrenCopy.size() == 0) {
            throw new IllegalArgumentException("please provide a valid List of children");
        }
        //adding all children to nodes with root as their only adjacent node, if child is already added throw exception
        nodes.add(root);
        root.addAdjacentNodeCollection(childrenCopy);
        for (IP child : childrenCopy) {
            if (nodes.contains(child)) {
                throw new IllegalArgumentException("Duplicate entry in child list");
            }
            nodes.add(child);
            child.addAdjacentNode(root);
        }
        //check if tree is valid
        if (!NetworkUtil.networkIsValid(List.copyOf(nodes))) {
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
        if (bracketNotation == null) {
            throw new ParseException(ErrorMessages.INVALID_BRACKET_NOTATION);
        }
        //trying to parse bracketNotation
        BracketNotationParser parser = new BracketNotationParser();
        try {
            parser.parse(bracketNotation);
        } catch (IOException e) {
            throw new ParseException(ErrorMessages.INVALID_BRACKET_NOTATION);
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

    /**
     * Tries to add new subnet to this Network. Checks validity of new Network using networkIsValid.
     * If new network is not valid no changes are made on this Network
     *
     * @param subnet the network to be added to this network
     * @return  whether the network was successfully changed
     */
    public boolean add(final Network subnet) {
        if (subnet == null) {
            return false;
        }
        //creating temporary nodes list to simulate adding of subnet
        List<IP> tempNodesList = NetworkUtil.deepCopy(nodes);
        // create deep copy list of subnet.nodes
        List<IP> subnetNodes = NetworkUtil.deepCopy(subnet.nodes);
        boolean networkChanged = true;
        boolean addedNewNodes = false;

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
                        addedNewNodes = true;
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
                        addedNewNodes = true;
                    } else {
                        //neither subNode nor subAdjNode are contained tempNodesList
                        //assumption: no need to add connection since they are connected already
                        tempNodesList.add(subAdjNode);
                        addedNewNodes = true;
                    }
                }
            }
        }
        //check if adding would destroy tree
        if (!NetworkUtil.networkIsValid(tempNodesList)) {
            return false;
        }
        //checking if anything changed

        if (!addedNewNodes) {
            networkChanged = false;
            outerLoop:
            for (IP node : tempNodesList) {
                for (IP adjNode : node.getAdjacentNodes()) {
                    if (!getIPFromList(node, nodes).getAdjacentNodes().contains(getIPFromList(adjNode, nodes))) {
                        networkChanged = true;
                        break outerLoop;
                    }
                }
            }
        }
        //add nodes and edges, duplicates are avoided automatically because of add implementation in set
        nodes = tempNodesList;
        //clear edges and generate new edges based on new nodes
        return networkChanged;
    }

    /**
     * returns sorted list of nodes using Collections.sort() which sorts the list in relation to the node's IP-values
     *
     * @return sorted nodes list
     */
    public List<IP> list() {
        List<IP> sortedNodes = NetworkUtil.deepCopy(nodes);
        Collections.sort(sortedNodes);
        return sortedNodes;
    }

    public boolean connect(final IP ip1, final IP ip2) {
        if (ip1 == null || ip2 == null || ip1.equals(ip2) || !nodes.contains(ip1) || !nodes.contains(ip2)
                || getIPFromList(ip1, nodes).getAdjacentNodes().contains(getIPFromList(ip2, nodes))) {
            return false;
        }
        getIPFromList(ip1, nodes).addAdjacentNode(getIPFromList(ip2, nodes));
        getIPFromList(ip2, nodes).addAdjacentNode(getIPFromList(ip1, nodes));
        if (NetworkUtil.networkIsValid(nodes)) {
            return true;

        }
        getIPFromList(ip1, nodes).getAdjacentNodes().remove(getIPFromList(ip2, nodes));
        getIPFromList(ip2, nodes).getAdjacentNodes().remove(getIPFromList(ip1, nodes));
        return false;
    }

    public boolean disconnect(final IP ip1, final IP ip2) {
        if (ip1 == null || ip2 == null || ip1.equals(ip2) || !nodes.contains(ip1) || !nodes.contains(ip2)
                || !getIPFromList(ip1, nodes).getAdjacentNodes().contains(getIPFromList(ip2, nodes))
                || nodes.size() == 2) {
            return false;
        }
        List<IP> currentNodes = new ArrayList<>(nodes);
        Set<IP> ip1AdjNodes = getIPFromList(ip1, currentNodes).getAdjacentNodes();
        Set<IP> ip2AdjNodes = getIPFromList(ip2, currentNodes).getAdjacentNodes();

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
        /*if (ip == null) {
            return false;
        }
        return NetworkUtil.contains(ip, nodes);

         */
        return nodes.contains(ip);
    }

    public int getHeight(final IP root) {
        if (root == null || !nodes.contains(root)) {
            return 0;
        }
        return NetworkUtil.getHeight(root, nodes);
    }

    public List<List<IP>> getLevels(final IP root) {
        List<List<IP>> output = NetworkUtil.getLevelsUnsorted(root, nodes);
        for (List<IP> level : output) {
            Collections.sort(level);
        }
        return output;
    }

    public List<IP> getRoute(final IP start, final IP end) {
        if (start == null || end == null || start.equals(end) || !nodes.contains(start) || !nodes.contains(end)) {
            return new LinkedList<>();
        }
        return NetworkUtil.getRoute(start, end, nodes);
    }

    public String toString(IP root) {
        if (root == null || !nodes.contains(root)) {
            return "";
        }
        IP thisRoot = getIPFromList(root, List.copyOf(nodes));
        BracketNotationPrinter printer = new BracketNotationPrinter();
        printer.print(NetworkUtil.getLevelsUnsorted(thisRoot, nodes));
        return printer.getBracketNotation();
    }
}
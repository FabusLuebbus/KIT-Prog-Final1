package src.network;

import src.exceptions.ErrorMessages;
import src.exceptions.ParseException;
import src.ip.IP;
import src.parsing.BracketNotationParser;
import src.printing.BracketNotationPrinter;
import static src.network.NetworkUtil.listIP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * an instance of Network represents a graph modelling a network of computers.
 * nodes are saved in a List of nodes with every node containing information about its adjacent nodes.
 * Therefore, the graph is fully defined. This class provides basic functionality regarding the network / the graph.
 *
 * @author usmsk
 * @version 2.1
 */
public class Network {
    private List<IP> nodes = new LinkedList<>();

    /**
     * generates a network of height 1. links all children to root and the other way round.
     * If:
     *      - one of arguments is / contains null
     *      - children list contains duplicate entry / contains root
     *  then no network is created.
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
     * @throws ParseException if parser fails to parse input or input is null
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
     * If subnet i null or the combined network is not valid all changes are discarded.
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
                        listIP(subNode, tempNodesList).addAdjacentNode(listIP(subAdjNode, tempNodesList));
                        listIP(subAdjNode, tempNodesList).addAdjacentNode(listIP(subNode, tempNodesList));
                    } else {
                        //subNode is contained in tempNodesList but subAdjNode not
                        listIP(subNode, tempNodesList).addAdjacentNode(subAdjNode);
                        //make subAdjNode point to correct node (in List)
                        subAdjNode.getAdjacentNodes().remove(subNode);
                        subAdjNode.addAdjacentNode(listIP(subNode, tempNodesList));
                        tempNodesList.add(subAdjNode);
                        addedNewNodes = true;
                    }
                }
            } else {
                tempNodesList.add(subNode);
                for (IP subAdjNode : subNode.getAdjacentNodes()) {
                    if (tempNodesList.contains(subAdjNode)) {
                        //subNode is not contained in tempNodeList but subAdjNode is
                        listIP(subAdjNode, tempNodesList).addAdjacentNode(subNode);
                        //make subNode point to correct node (in List)
                        subNode.getAdjacentNodes().remove(subAdjNode);
                        subNode.addAdjacentNode(listIP(subAdjNode, tempNodesList));
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
                    if (!listIP(node, nodes).getAdjacentNodes().contains(listIP(adjNode, nodes))) {
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

    /**
     * tries to connect two existing, unconnected nodes to each other.
     *
     * @param ip1 node 1 which is to be connected to:
     * @param ip2 node 2
     * @return whether the nodes were connected: false, if connection would lead to invalid tree, one of the IPs
     *          is null, both IPs are the same, nodes are already connected, one of the nodes does not exist in network.
     */
    public boolean connect(final IP ip1, final IP ip2) {
        if (ip1 == null || ip2 == null || ip1.equals(ip2) || !nodes.contains(ip1) || !nodes.contains(ip2)
                || listIP(ip1, nodes).getAdjacentNodes().contains(listIP(ip2, nodes))) {
            return false;
        }
        listIP(ip1, nodes).addAdjacentNode(listIP(ip2, nodes));
        listIP(ip2, nodes).addAdjacentNode(listIP(ip1, nodes));
        if (NetworkUtil.networkIsValid(nodes)) {
            return true;

        }
        listIP(ip1, nodes).getAdjacentNodes().remove(listIP(ip2, nodes));
        listIP(ip2, nodes).getAdjacentNodes().remove(listIP(ip1, nodes));
        return false;
    }

    /**
     * tries to disconnect two existing, connected nodes in network. If one of the nodes is not connected to any other
     * node afterwards this node is then deleted.
     *
     * @param ip1 node 1 which is to be disconnected from:
     * @param ip2 node 2
     * @return whether the nodes were succesfully disconnected: false, if one of the IPs is null, both IPs are the same,
     *          nodes are not even connected, one of the nodes does not exist in network.
     */
    public boolean disconnect(final IP ip1, final IP ip2) {
        if (ip1 == null || ip2 == null || ip1.equals(ip2) || !nodes.contains(ip1) || !nodes.contains(ip2)
                || !listIP(ip1, nodes).getAdjacentNodes().contains(listIP(ip2, nodes))
                || nodes.size() == 2) {
            return false;
        }
        List<IP> currentNodes = new ArrayList<>(nodes);
        Set<IP> ip1AdjNodes = listIP(ip1, currentNodes).getAdjacentNodes();
        Set<IP> ip2AdjNodes = listIP(ip2, currentNodes).getAdjacentNodes();

        ip1AdjNodes.remove(listIP(ip2, currentNodes));
        ip2AdjNodes.remove(listIP(ip1, currentNodes));
        if (ip1AdjNodes.size() == 0) {
            nodes.remove(listIP(ip1, currentNodes));
        }
        if (ip2AdjNodes.size() == 0) {
            nodes.remove(listIP(ip2, currentNodes));
        }
        return true;
    }

    /**
     * checks if network contains a given IP by using contains method on nodes list
     *
     * @param ip IP to be checked for appearance in network
     * @return whether ip could be found and is not null
     */
    public boolean contains(final IP ip) {
        return nodes.contains(ip) && ip != null;
    }

    /**
     * checks if root is null and if it exists in network. Then gets height using method from NetworkUtil
     *
     * @param root node which serves as temporary root to determine height
     * @return height (depending on chosen root)
     */
    public int getHeight(final IP root) {
        if (root == null || !nodes.contains(root)) {
            return 0;
        }
        return NetworkUtil.getHeight(root, nodes);
    }

    /**
     * gets a list containing lists while every part-list represents a level of the graph (network).
     *
     * @param root node which serves as temporary root to determine levels
     * @return levels (depending on chosen root)
     */
    public List<List<IP>> getLevels(final IP root) {
        List<List<IP>> output = NetworkUtil.getLevelsUnsorted(root, nodes);
        for (List<IP> level : output) {
            Collections.sort(level);
        }
        return output;
    }

    /**
     * checks start / end is null, start equals end, start / end is not contained in network. If so returns empty list.
     * else returns list containing route. This list is retrieved from method in NetworkUtil
     *
     * @param start start node of rout
     * @param end end node of rout
     * @return list of all nodes on route in correct order
     */
    public List<IP> getRoute(final IP start, final IP end) {
        if (start == null || end == null || start.equals(end) || !nodes.contains(start) || !nodes.contains(end)) {
            return new LinkedList<>();
        }
        return NetworkUtil.getRoute(start, end, nodes);
    }

    /**
     * checks if root is not null and exists in network then retrieves bracket notation of current network from
     * BracketNotationPrinter
     *
     * @param root node which serves as temporary root to determine bracket notation
     * @return bracket notation (depending on chosen root)
     */
    public String toString(IP root) {
        if (root == null || !nodes.contains(root)) {
            return "";
        }
        IP thisRoot = listIP(root, List.copyOf(nodes));
        BracketNotationPrinter printer = new BracketNotationPrinter();
        printer.print(NetworkUtil.getLevelsUnsorted(thisRoot, nodes));
        return printer.getBracketNotation();
    }
}
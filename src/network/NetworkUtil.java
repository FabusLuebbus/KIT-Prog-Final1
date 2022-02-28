package src.network;

import src.exceptions.ParseException;
import src.ip.IP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * class which provides further methods and functions used in network. Main purpose of this class is to clean up code in
 * Network and extracting methods which provide functionality useful for general trees (not only networks);
 *
 * @author usmsk
 * @version 1.2
 */
public final class NetworkUtil {
    private NetworkUtil() {
    }

    /**
     * gets height of a tree in relation to given root by recursing to the lowest level of tree and counting while
     * going back up
     *
     * @param root node which is to be used as root
     * @param nodes List of nodes (the 'tree')
     * @return height of tree (dependent on root)
     */
    public static int getHeight(IP root, List<IP> nodes) {
        for (IP node : nodes) {
            node.setParent(null);
        }
        List<IP> currentNodes = new ArrayList<>(nodes);
        IP currentRoot = listIP(root, currentNodes);
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

    /**
     * checks if root is null or does not exist in nodes. If so an empty list is returned. Else gets List with
     * a list at every entry containing one level of the tree each. This method uses a custom implementation of BFS
     *
     * @param root node which is to be used as root
     * @param nodes List of nodes (the 'tree')
     * @return list containing levels of deep copied nodes
     */
    public static List<List<IP>> getLevelsUnsorted(IP root, List<IP> nodes) {
        List<List<IP>> output = new LinkedList<>();
        if (!nodes.contains(root) || root == null) {
            return output;
        }
        List<IP> currentNodes;

        currentNodes = deepCopy(nodes);

        int currLevel = 0;
        output.add(new LinkedList<>());
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
        queue.add(listIP(root, currentNodes));
        queue.add(levelMarker);
        listIP(root, currentNodes).setVisited(true);
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
            if (queue.peek() == null) {
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
            if (queue.peek() != null && queue.peek().equals(levelMarker)) {
                queue.add(levelMarker);
            }
        }
        return output;
    }

    /**
     * gets a list of ips representing the route from start node to end node. This method uses an implementation of BFS.
     *
     * @param start start node of route
     * @param end end node of route
     * @param nodes List of nodes (the 'tree')
     * @return list containing all nodes on root in correct order (nodes are deep copied nodes equal to the nodes in
     *          network)
     */
    public static List<IP> getRoute(IP start, IP end, List<IP> nodes) {
        for (IP node : nodes) {
            node.setParent(null);
            node.setVisited(false);
        }
        //implement queue
        Queue<IP> queue = new LinkedList<>();
        List<IP> route = new LinkedList<>();
        IP root = listIP(start, List.copyOf(nodes));
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
        route.add(0, current);
        while (!current.equals(start)) {
            route.add(0, current.getParent());
            current = current.getParent();
        }
        return NetworkUtil.deepCopy(route);
    }

    /**
     * method to get a node equal to given given from nodes
     *
     * @param ip node to be found and returned
     * @param nodes list of existing nodes
     * @return the node equal to ip but contained in nodes
     */
    public static IP listIP(IP ip, List<IP> nodes) {
        return nodes.get(nodes.indexOf(ip));
    }

    /**
     * provides a deep copy of a list of nodes also deep copying every element of the list
     *
     * @param nodes list of nodes to be copied
     * @return deep copy of nodes
     */
    public static List<IP> deepCopy(Collection<IP> nodes) {
        List<IP> nodesCopy = new LinkedList<>();
        for (IP node : nodes) {
            IP nodeClone;
            try {
                nodeClone = new IP(node.toString());
            } catch (ParseException | NullPointerException e) {
                return new ArrayList<>();
            }
            nodesCopy.add(nodeClone);
        }

        for (IP node : nodes) {
            if (nodesCopy.contains(node)) {
                //making sure to reference clone in List
                IP nodeClone = listIP(node, nodesCopy);
                IP adjNodeClone;
                //iterating over original node's adjacency list to provide copy as adjacency list to clones
                for (IP adjacentNode : node.getAdjacentNodes()) {
                    if (nodesCopy.contains(adjacentNode)) {
                        adjNodeClone = listIP(adjacentNode, nodesCopy);
                        nodeClone.addAdjacentNode(adjNodeClone);
                        adjNodeClone.addAdjacentNode(nodeClone);
                    }
                }
            }
        }
        return nodesCopy;
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
    public static boolean networkIsValid(List<IP> nodes) {
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
}

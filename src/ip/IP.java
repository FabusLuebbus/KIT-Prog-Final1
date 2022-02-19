package src.ip;

import src.exceptions.ParseException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * class to provide functionality regarding IP-Addresses
 *
 * @author usmsk
 * @version 1.0
 */
public class IP implements Comparable<IP> {
    /**
     * regex stating correct IP-Address format
     */
    public static final String IP_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"
            + "(\\.([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3})$";

    private final long ipValue;
    //used in BFS not a final variable changes depending on interpretation
    private IP parent = null;
    //used in BFS
    private boolean visited = false;
    private int level = 0;
    private final Set<IP> adjacentNodes = new HashSet<>();

    /**
     * constructor for IPs.
     * checks if specified IP matches IP_PATTERN.
     * then builds ipAsBinary by separately interpreting the 4 sections as 8bit binary numbers and concatenating them.
     *
     * @param pointNotation Ip address in point notation
     * @throws ParseException if ipAddress does not match proper point Notation(IP_PATTERN)
     */
    public IP(final String pointNotation) throws ParseException {
        if ((!pointNotation.matches(IP_PATTERN))) {
            throw new ParseException("This IP-Address is not valid. It does not match the required pattern");
        }

        String[] pointNotationArray = pointNotation.split("\\.");
        StringBuilder ipAsBinary = new StringBuilder();

        for (String s : pointNotationArray) {
            ipAsBinary.append(String.format("%8s", Long.toBinaryString(Long.parseLong(s))).replace(' ', '0'));
        }
        ipValue = Long.parseLong(ipAsBinary.toString(), 2);
    }

    /**
     * adds given IP to Set of adjacent nodes
     *
     * @param ip IP to be added to adjacent nodes
     */
    public void addAdjacentNode(IP ip) {
        adjacentNodes.add(ip);
    }

    /**
     * adds all elements of given collection containing IPs to adjacentNodes Set
     *
     * @param coll collection containing all elements to be added
     */
    public void addAdjacentNodeCollection(Collection<? extends IP> coll) {
        adjacentNodes.addAll(coll);
    }

    /**
     * getter method for adjacent nodes
     *
     * @return reference to this.adjacentNodes (no deep copy)
     */
    public Set<IP> getAdjacentNodes() {
        return adjacentNodes;
    }

    /**
     * setter method for visited parameter used in breadth searching algorithms
     *
     * @param b true is visited, false if not
     */
    public void setVisited(boolean b) {
        visited = b;
    }


    /**
     * setter method for parent variable used in breadth searching algorithms
     *
     * @param parent IP from which this IP was reached
     */
    public void setParent(IP parent) {
        this.parent = parent;
    }

    /**
     * getter method for visited parameter
     *
     * @return true if visited, false if not
     */
    public boolean getVisited() {
        return visited;
    }

    /**
     * getter method for parent variable
     *
     * @return IP from which this IP was reached during searching algorithm
     */
    public IP getParent() {
        return parent;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * getter method for binary interpretation (without '.') of ip as string
     *
     * @return binary interpretation
     */
    public long getIpValue() {
        return ipValue;
    }

    @Override
    public String toString() {
        String temp = Long.toBinaryString(ipValue);
        temp = String.format("%32s", temp).replace(' ', '0');
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            output.append(Long.parseLong(temp.substring(0, 8), 2));
            if (i < 3) {
                output.append('.');
            }
            temp = temp.substring(8);
        }
        return output.toString();
    }

    @Override
    public int compareTo(IP o) {
        return Long.compare(ipValue, o.ipValue);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IP)) {
            return false;
        }

        return compareTo((IP) o) == 0;
    }

    @Override
    public int hashCode() {
        return 42;
    }
}

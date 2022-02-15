package src.ip;

import src.exceptions.ParseException;

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

    /**
     * contains binary interpretation of IP-Address
     */
    private final String ipAsBinary;

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

        String rest = pointNotation;
        String temp;
        StringBuilder ipAsBinary = new StringBuilder();
        int indexOfFirstPoint;
        for (int i = 0; i < 4; i++) {
            indexOfFirstPoint = rest.indexOf('.');
            temp = rest.substring(0, indexOfFirstPoint);  //interpretation as 8bit binary and concatenation
            ipAsBinary.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(temp)))
                    .replace(' ', '0'));
            if (i < 2) {
                rest = rest.substring(++indexOfFirstPoint);
            }
        }
        this.ipAsBinary = ipAsBinary.toString();
    }

    public String getIpAsBinary() {
        return ipAsBinary;
    }

    @Override
    public String toString() {
        String temp = ipAsBinary;
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            output.append(Integer.parseInt(temp.substring(0, 8), 2));
            if (i < 3) {
                output.append('.');
                temp = temp.substring(8);
            }
            
        }
        return output.toString();
    }

    @Override
    public int compareTo(IP o) {
        if (Long.parseLong(o.ipAsBinary, 2) > Long.parseLong(this.ipAsBinary, 2)) {
            return -1;
        } else if (Long.parseLong(o.ipAsBinary, 2) < Long.parseLong(this.ipAsBinary, 2)) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == IP.class) {
            return compareTo((IP) o) == 0;
        }
        return false;
    }
}

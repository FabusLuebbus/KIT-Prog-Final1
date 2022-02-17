package src.network;
import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class FurtherNetworkTests {
    @Test
    public void bfsTest() throws ParseException {
        List<IP> ips = new LinkedList<>();
        Network network = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        network.getEdges().add(new Edge(new IP("1.1.1.1"), new IP("2.2.2.2")));
        network.getNodes().add(new IP("13.13.13.13"));
        List<IP> nodes = List.copyOf(network.getNodes());
        assertFalse(network.networkIsValid(nodes));
    }

    @Test
    public void addTest() throws ParseException {
        Network network1 = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        Network network2 = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        Network network3 = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))");
        Network subnetFail = new Network("(1.1.1.1 2.2.2.2)");
        Network subnetSuccess = new Network("(1.1.1.1 3.3.3.3)");
        Network subnetFail3 = new Network("(0.146.197.108 34.49.145.239)");
        Network subnetSuccess3 = new Network("(141.255.1.133 0.0.0.0)");
        assertTrue(network3.add(subnetSuccess3));
        assertTrue(network1.add(subnetSuccess));
        assertFalse(network2.add(subnetFail));
        assertFalse(network3.add(subnetFail3));
        for (IP ip : network2.getNodes()) {
            System.out.print(ip + " | ");

        }
        System.out.println("");
        for (IP ip : network2.getNodes()) {
            System.out.print(System.identityHashCode(ip) + " | ");

        }
        System.out.println("");
        for (Edge edge : network2.getEdges()) {
            System.out.print(edge.getFirstNode());
            System.out.print(" | ");
            System.out.print(edge.getSecondNode());
            System.out.println("");
            System.out.print(System.identityHashCode(edge.getFirstNode()));
            System.out.print(" | ");
            System.out.print(System.identityHashCode(edge.getSecondNode()));
            System.out.println("");
        }
    }
    




    @Test(expected = ParseException.class)
    public void newParserTest() throws IOException, ParseException {
        BracketNotationParser parser = new BracketNotationParser();
        //assertTrue(parser.parse("(0.0.0.0 1.1.1.1)"));
        //assertTrue(parser.parse("(0.0.0.0 (1.1.1.1 2.2.2.2))"));
        //assertTrue(parser.parse("(122.117.67.158 (141.255.1.133 0.146.197.108))"));
        //assertTrue(parser.parse("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))"));
        parser.parse("(0.0.0.0)");

/*

parser.parse("(0.0.0.0 (1.1.1.1))");
parser.parse("(0.0.0.0(1.1.1.1 2.2.2.2))");
parser.parse("(0.0.0.0 * ");
parser.parse("+");
parser.parse(",");
parser.parse(" ");
parser.parse("- ");
parser.parse("/");
parser.parse("  ");
*/


        System.out.println(parser.getNodes().toString());
        for (IP node : parser.getNodes()) {
            System.out.println(node.getAdjacentNodes().toString());
        }
    }
    @Test
    public void bracketNetworkConstructorTest() throws ParseException {
        //Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))");
        Network network = new Network("(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 (4.4.4.4 (5.5.5.5))))))");
        System.out.println(network.getNodes().toString());
        for (IP node : network.getNodes()) {
            for (IP adjacentNode : node.getAdjacentNodes()) {
                System.out.print(adjacentNode.toString() + " | ");
            }
            System.out.println("");
        }
        System.out.println(network.getEdges().toString());
        System.out.println(network.getEdges().size());
    }
}

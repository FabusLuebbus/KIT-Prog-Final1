package src.network;
import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;
import src.parsing.BracketNotationParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class FurtherNetworkTests {
    @Test
    public void bfsTest() throws ParseException {
        List<IP> ips = new LinkedList<>();
        Network network = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        network.getNodes().add(new IP("13.13.13.13"));
        List<IP> nodes = List.copyOf(network.getNodes());

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
    }

    @Test
    public void sortTest() throws ParseException {
        Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77) 9.9.9.9 8.8.8.8 7.7.7.7 6.6.6.6 5.5.5.5 1.1.1.1 4.4.4.4 3.3.3.3 2.2.2.2)");
        List<IP> list = network.list();
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getIpValue() < list.get(i + 1).getIpValue());
        }
    }

    @Test
    public void connectAndDisconnectTest() throws ParseException {
        Network network = new Network("(255.255.255.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77) 9.9.9.9 8.8.8.8 7.7.7.7 6.6.6.6 5.5.5.5 1.1.1.1 4.4.4.4 3.3.3.3 2.2.2.2)");
        Network network1 = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        Network network2 = new Network("(3.3.3.3 (4.4.4.4 5.5.5.5) 6.6.6.6)");
        assertFalse(network1.connect(new IP("1.1.1.1"),  new IP("2.2.2.2")));
        network1.add(network2);
        assertTrue(network1.connect(new IP("1.1.1.1"),  new IP("3.3.3.3")));
        assertFalse(network1.connect(new IP("85.193.148.255"), new IP("0.146.197.108")));
        assertTrue(network1.disconnect(new IP("1.1.1.1"),  new IP("3.3.3.3")));
        assertTrue(network1.disconnect(new IP("1.1.1.1"),  new IP("0.0.0.0")));
        assertFalse(network1.disconnect(new IP("0.0.0.0"),  new IP("2.2.2.2")));
        assertTrue(network1.disconnect(new IP("6.6.6.6"),  new IP("3.3.3.3")));
        assertTrue(network1.disconnect(new IP("4.4.4.4"),  new IP("3.3.3.3")));
        assertFalse(network1.disconnect(new IP("4.4.4.4"),  new IP("5.5.5.5")));
    }

    @Test
    public void containsTest() throws ParseException {
        Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77) 9.9.9.9 8.8.8.8 7.7.7.7 6.6.6.6 5.5.5.5 1.1.1.1 4.4.4.4 3.3.3.3 2.2.2.2)");
        assertTrue(network.contains(new IP("85.193.148.255")));
        assertTrue(network.contains(new IP("122.117.67.158")));
        assertTrue(network.contains(new IP("34.49.145.239")));
        assertTrue(network.contains(new IP("116.132.83.77")));
        assertTrue(network.contains(new IP("8.8.8.8")));
        assertFalse(network.contains(new IP("0.0.0.0")));
    }

    @Test
    public void getHeightTest() throws ParseException {
        Network network1 = new Network("(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 (4.4.4.4 (5.5.5.5 6.6.6.6))))))");
        Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77) 9.9.9.9 8.8.8.8 7.7.7.7 6.6.6.6 5.5.5.5 1.1.1.1 4.4.4.4 3.3.3.3 2.2.2.2)");
        assertEquals(5, network1.getHeight(new IP("5.5.5.5")));
        assertEquals(6, network1.getHeight(new IP("0.0.0.0")));
        assertEquals(3, network1.getHeight(new IP("3.3.3.3")));
        assertEquals(6, network1.getHeight(new IP("0.0.0.0")));
        assertEquals(4, network.getHeight(new IP("77.135.84.171")));
        network.getLevels(new IP("85.193.148.255"));
    }

    @Test
    public void getRouteTest() throws ParseException {
        Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77) 9.9.9.9 8.8.8.8 7.7.7.7 6.6.6.6 5.5.5.5 1.1.1.1 4.4.4.4 3.3.3.3 2.2.2.2)");
        network.getRoute(new IP("77.135.84.171"), new IP("122.117.67.158"));
    }
}

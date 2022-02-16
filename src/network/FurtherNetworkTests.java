package src.network;
import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class FurtherNetworkTests {
    @Test
    public void bfsTest() throws ParseException {
        List<IP> ips = new LinkedList<>();
        for (int i = 1; i < 11; i++) {
            ips.add(new IP(i + "." + i + "." + i + "." + i));
        }
        Network network = new Network(new IP("0.0.0.0"), ips);
        network.getNodes().add(new IP("13.13.13.13"));
        List<IP> nodes = List.copyOf(network.getNodes());
        assertFalse(network.treeIsValid(nodes));
    }

    /*@Test
    public void ddTest() throws ParseException {
        Network network = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        Network subnetFail = new Network("(1.1.1.1 2.2.2.2)");
        Network subnetSuccess = new Network("(1.1.1.1 3.3.3.3)");
        assertTrue(network.add(subnetSuccess));
        assertFalse(network.add(subnetFail));
    }
    
     */



    @Test//(expected = ParseException.class)
    public void newParserTest() throws IOException, ParseException {
        AlternateBracketParser parser = new AlternateBracketParser();
        //assertTrue(parser.parse("(0.0.0.0 1.1.1.1)"));
        //assertTrue(parser.parse("(0.0.0.0 (1.1.1.1 2.2.2.2))"));
        //assertTrue(parser.parse("(122.117.67.158 (141.255.1.133 0.146.197.108))"));
        assertTrue(parser.parse("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))"));
        //parser.parse("(0.0.0.0)");

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
        Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))");
        System.out.println(network.getNodes().toString());
        System.out.println(network.getEdges().toString());
    }
}

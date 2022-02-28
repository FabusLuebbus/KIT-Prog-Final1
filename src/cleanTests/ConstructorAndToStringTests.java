package src.cleanTests;

import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;
import src.network.Network;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class ConstructorAndToStringTests {
    IP nullIP = null;
    List<IP> nullList = null;
    List<IP> emptyChildrenList = new LinkedList<>();
    public static final String DEEPLY_NESTED = "(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 (4.4.4.4 (5.5.5.5 (6.6.6.6 7.7.7.7)))))))";
    public static final String NETWORK_FROM_EXAMPLE = "(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";

    @Test(expected = IllegalArgumentException.class)
    public void nullRootTest() throws ParseException {
        Network network = new Network(nullIP, List.of(new IP("1.1.1.1")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullChildrenList() throws ParseException {
        Network network = new Network(new IP("1.1.1.1"), nullList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullChildInList() throws ParseException {
        emptyChildrenList.add(nullIP);
        Network network = new Network(new IP("1.1.1.1"), emptyChildrenList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyChildrenList() throws ParseException {
        Network network = new Network(new IP("1.1.1.1"), emptyChildrenList);
    }

    @Test
    public void creationAndToString() throws ParseException {
        Network network = new Network(new IP("0.0.0.0"), List.of(new IP("3.3.3.3"), new
                IP("2.2.2.2"), new IP("1.1.1.1")));
        Network network1 = new Network("(4.4.4.4 5.5.5.5)");
        assertEquals("(0.0.0.0 1.1.1.1 2.2.2.2 3.3.3.3)", network.toString(new IP("0.0.0.0")));
        assertEquals("(2.2.2.2 (0.0.0.0 1.1.1.1 3.3.3.3))" , network.toString(new IP("2.2.2.2")));
        network.add(network1);
        network.connect(new IP("3.3.3.3"), new IP("4.4.4.4"));
        assertEquals("(0.0.0.0 1.1.1.1 2.2.2.2 (3.3.3.3 (4.4.4.4 5.5.5.5)))", network.toString(new IP("0.0.0.0")));
        network.disconnect(new IP("3.3.3.3"), new IP("4.4.4.4"));
        network.add(new Network("(3.3.3.3 4.4.4.4)"));
        assertEquals("(0.0.0.0 1.1.1.1 2.2.2.2 (3.3.3.3 (4.4.4.4 5.5.5.5)))", network.toString(new IP("0.0.0.0")));
    }

    @Test(expected = ParseException.class)
    public void ipFail1() throws ParseException {
        IP ip = new IP("");
    }

    @Test(expected = ParseException.class)
    public void ipFail2() throws ParseException {
        IP ip = new IP("00000000000000000000000000000000");
    }

    @Test(expected = ParseException.class)
    public void ipFail3() throws ParseException {
        IP ip = new IP("000000000000");
    }

    @Test(expected = ParseException.class)
    public void ipFail4() throws ParseException {
        IP ip = new IP("-1.-1.-1.-1.");
    }

    @Test(expected = ParseException.class)
    public void ipFail5() throws ParseException {
        IP ip = new IP("256.1.1.1");
    }

    @Test(expected = ParseException.class)
    public void ipFail6() throws ParseException {
        IP ip = new IP("1.256.1.1");
    }

    @Test(expected = ParseException.class)
    public void ipFail7() throws ParseException {
        IP ip = new IP("1.1.256.1");
    }

    @Test(expected = ParseException.class)
    public void ipFail8() throws ParseException {
        IP ip = new IP("1.1.1.256");
    }

    @Test(expected = ParseException.class)
    public void constructorEmptyString() throws ParseException {
        Network network = new Network("");
    }

    @Test
    public void creationAndToString2() throws ParseException {
        Network network = new Network(DEEPLY_NESTED);
        assertEquals("(6.6.6.6 (5.5.5.5 (4.4.4.4 (3.3.3.3 (2.2.2.2 (1.1.1.1 0.0.0.0))))) 7.7.7.7)", network.toString(new IP("6.6.6.6")));
        assertEquals("(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 (4.4.4.4 (5.5.5.5 (6.6.6.6 7.7.7.7)))))))", network.toString(new IP("0.0.0.0")));
        assertEquals("(4.4.4.4 (3.3.3.3 (2.2.2.2 (1.1.1.1 0.0.0.0))) (5.5.5.5 (6.6.6.6 7.7.7.7)))", network.toString(new IP("4.4.4.4")));
        Network network2 = new Network("(0.0.0.0 (1.1.1.1 2.2.2.2 7.7.7.7) (3.3.3.3 4.4.4.4 8.8.8.8) 10.10.10.10 (5.5.5.5 6.6.6.6 9.9.9.9))");
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        assertEquals("(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))", network1.toString(new IP("85.193.148.81")));
        assertEquals("(10.10.10.10 (0.0.0.0 (1.1.1.1 2.2.2.2 7.7.7.7) (3.3.3.3 4.4.4.4 8.8.8.8) (5.5.5.5 6.6.6.6 9.9.9.9)))", network2.toString(new IP("10.10.10.10")));
    }

    @Test(expected = ParseException.class)
    public void creationFail() throws ParseException {
        Network network = new Network("(1.1.1.1 (2.2.2.2 3.3.3.3) 4.4.4.4 (5.5.5.5))");
    }

    @Test
    public void addTests() throws ParseException {
        Network network = new Network(DEEPLY_NESTED);
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        assertFalse(network.add(new Network("(0.0.0.0 1.1.1.1)")));
        assertFalse(network.add(new Network("(1.1.1.1 2.2.2.2)")));
        assertFalse(network.add(new Network("(2.2.2.2 3.3.3.3)")));
        assertFalse(network.add(new Network("(3.3.3.3 4.4.4.4)")));
        assertFalse(network.add(new Network("(4.4.4.4 5.5.5.5)")));
        assertFalse(network.add(new Network("(5.5.5.5 6.6.6.6)")));
        assertFalse(network.add(new Network("(6.6.6.6 7.7.7.7)")));
        assertTrue(network.add(network1));
        assertEquals("(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))", network.toString(new IP("85.193.148.81")));
        assertTrue(network.add(new Network("(77.135.84.171 4.4.4.4)")));
        assertEquals("(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 "
                + "(77.135.84.171 (4.4.4.4 (3.3.3.3 (2.2.2.2 (1.1.1.1 0.0.0.0))) (5.5.5.5 (6.6.6.6 7.7.7.7)))) 116.132.83.77 252.29.23.0))",
                network.toString(new IP("85.193.148.81")));
    }

    @Test(expected = ParseException.class)
    public void leadingWhitespaceTest() throws ParseException {
        Network network = new Network(" (1.1.1.1 2.2.2.2)");
    }

    @Test
    public void getRouteTest() throws ParseException {
        Network network = new Network("(1.1.1.1 (2.2.2.2 3.3.3.3) 4.4.4.4)");
        network.disconnect(new IP("1.1.1.1"), new IP("2.2.2.2"));
        System.out.println(network.getRoute(new IP("1.1.1.1"), new IP("4.4.4.4")));
    }
}
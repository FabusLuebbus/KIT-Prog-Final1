package src.cleanTests;

import org.junit.Test;
import src.exceptions.ParseException;
import src.ip.IP;
import src.network.Network;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;


public class TestsP {

    //assets stolen from Fabse
    /**
     * The Null ip.
     */
    IP nullIP = null;
    /**
     * The Null list.
     */
    List<IP> nullList = null;
    /**
     * The Empty children list.
     */
    List<IP> emptyChildrenList = new LinkedList<>();
    /**
     * The constant DEEPLY_NESTED.
     */
    public static final String DEEPLY_NESTED = "(0.0.0.0 (1.1.1.1 (2.2.2.2 (3.3.3.3 (4.4.4.4 (5.5.5.5 (6.6.6.6 7.7.7.7)))))))";
    /**
     * The constant NETWORK_FROM_EXAMPLE.
     */
    public static final String NETWORK_FROM_EXAMPLE = "(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";

    //new asset range
    public static final String EXAMPLE_ROOT_STRING = "85.193.148.81";
    public static final String EXAMPLE_ADDITION1 = "(85.193.148.81 0.0.0.0 1.1.1.1)";
    public static final String EXAMPLE_ADDITION_RESULT1 = "(85.193.148.81 0.0.0.0 1.1.1.1 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";
    public static final String EXAMPLE_ADDITION2 = "(85.193.148.81 230.0.0.0 255.1.1.1)";
    public static final String EXAMPLE_ADDITION_RESULT2 = "(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) 230.0.0.0 (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0) 255.1.1.1)";
    public static final String EXAMPLE_DISCONNECT = "(85.193.148.81 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";
    public static final String EXAMPLE_INDEPENDENT = "(0.0.0.0 1.1.1.1)";
    public static final String EXAMPLE_INDEPENDENT_CONNECTED = "(85.193.148.81 (0.0.0.0 1.1.1.1) 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))";

    @Test
    public void networkAdding() throws ParseException {
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        Network network2 = new Network(EXAMPLE_ADDITION1);

        //add networks
        network1.add(network2);
        assertEquals(EXAMPLE_ADDITION_RESULT1, network1.toString(new IP(EXAMPLE_ROOT_STRING)));
    }

    @Test
    public void networkAddingOrderCheck() throws ParseException {
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        Network network2 = new Network(EXAMPLE_ADDITION2);

        //add networks
        network1.add(network2);
        //assert order
        assertEquals(EXAMPLE_ADDITION_RESULT2, network1.toString(new IP(EXAMPLE_ROOT_STRING)));

    }

    @Test
    public void toStringCheck() throws ParseException {
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        Network network2 = new Network(EXAMPLE_ADDITION1);

        //assert that the network toString works properly
        assertEquals(NETWORK_FROM_EXAMPLE, network1.toString(new IP(EXAMPLE_ROOT_STRING)));
        assertEquals(EXAMPLE_ADDITION1, network2.toString(new IP(EXAMPLE_ROOT_STRING)));
    }

    @Test
    public void networkChangeAfterAddition() throws ParseException {
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        Network network2 = new Network(EXAMPLE_ADDITION2);
        //add networks
        network1.add(network2);
        //change network 2
        network2.disconnect(new IP("85.193.148.81"), new IP("230.0.0.0"));
        assertEquals(EXAMPLE_ADDITION_RESULT2, network1.toString(new IP(EXAMPLE_ROOT_STRING)));
        System.out.println(network2.toString(new IP(EXAMPLE_ROOT_STRING)));
    }

    @Test
    public void networkDisconnect() throws ParseException {
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        IP ip1 = new IP(EXAMPLE_ROOT_STRING);
        IP ip2 = new IP("34.49.145.239");
        network1.disconnect(ip1, ip2);
        assertEquals(EXAMPLE_DISCONNECT, network1.toString(new IP(EXAMPLE_ROOT_STRING)));
    }

    @Test
    public void networkConnect() throws ParseException {
        Network network1 = new Network(NETWORK_FROM_EXAMPLE);
        Network network2 = new Network(EXAMPLE_INDEPENDENT);
        network1.add(network2);
        // check if network 1 contains both trees
        assertEquals(NETWORK_FROM_EXAMPLE, network1.toString(new IP(EXAMPLE_ROOT_STRING)));
        assertEquals(EXAMPLE_INDEPENDENT, network1.toString(new IP("0.0.0.0")));

        // connect trees
        network1.connect(new IP(EXAMPLE_ROOT_STRING), new IP("0.0.0.0"));

        // check if network string now contains both trees
        assertEquals(EXAMPLE_INDEPENDENT_CONNECTED, network1.toString(new IP(EXAMPLE_ROOT_STRING)));
    }

    @Test
    public void networkAddIdentical() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        network.add(network);
        assertEquals(NETWORK_FROM_EXAMPLE, network.toString(new IP(EXAMPLE_ROOT_STRING)));
    }

    @Test
    public void networkList() throws ParseException {
        Network network = new Network(EXAMPLE_INDEPENDENT_CONNECTED);
        List<IP> contents = List.of(new IP("0.0.0.0"), new IP("0.146.197.108"), new IP("1.1.1.1"), new IP("34.49.145.239"),
                new IP("39.20.222.120"), new IP("77.135.84.171"), new IP("85.193.148.81"), new IP("116.132.83.77"),
                new IP("122.117.67.158"), new IP("141.255.1.133"), new IP("231.189.0.127"), new IP("252.29.23.0"));
        assertEquals(contents, network.list());
    }


    // Range: network tests for null input

    @Test
    public void networkAddNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        assertFalse(network.add(null));
    }

    @Test
    public void disconnectNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        assertFalse(network.disconnect(null, null));
    }

    @Test
    public void connectNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        assertFalse(network.connect(null, null));
    }

    @Test
    public void containsNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        assertFalse(network.contains(null));
    }

    @Test
    public void getHeightNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        assertEquals(0, network.getHeight(null));
    }

    @Test
    public void getLevelsNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        List<List<IP>> levels = network.getLevels(null);
        assertEquals(0, levels.size());
    }

    @Test
    public void getRouteNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        List<IP> route = network.getRoute(null, new IP(EXAMPLE_ROOT_STRING));
        assertEquals(0, route.size());
        route = network.getRoute(new IP(EXAMPLE_ROOT_STRING), null);
        assertEquals(0, route.size());
        route = network.getRoute(null, null);
        assertEquals(0, route.size());
    }

    @Test
    public void toStringNull() throws ParseException {
        Network network = new Network(NETWORK_FROM_EXAMPLE);
        assertEquals("", network.toString(null));
    }
}

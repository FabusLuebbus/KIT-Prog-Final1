package src.network;
import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;

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

    @Test
    public void ddTest() throws ParseException {
        Network network = new Network("(0.0.0.0 1.1.1.1 2.2.2.2)");
        Network subnetFail = new Network("(1.1.1.1 2.2.2.2)");
        Network subnetSuccess = new Network("(1.1.1.1 3.3.3.3)");
        assertTrue(network.add(subnetSuccess));
        assertFalse(network.add(subnetFail));
    }
}

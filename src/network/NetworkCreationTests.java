package src.network;
import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;


import java.util.List;


import static org.junit.Assert.*;


public class NetworkCreationTests {
    IP root = new IP("0.0.0.0");
    IP child1 = new IP("1.1.1.1");
    IP child2 = new IP("2.2.2.2");
    public List<List<IP>> nodeList = List.of(List.of(root), List.of(child1, child2));

    public NetworkCreationTests() throws ParseException {
    }


    @Test
    public void TestFirstConstructor() throws ParseException {
        Network network = new Network(new IP("0.0.0.0"), nodeList.get(1));

        assertEquals("[1.1.1.1, 2.2.2.2, 0.0.0.0]", network.getNodes().toString());
        assertEquals("[(0.0.0.0, 1.1.1.1), (0.0.0.0, 2.2.2.2)]", network.getEdges().toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void expectedIllegalArgFirstConst() throws ParseException {
        Network network = new Network(new IP("0.0.0.0"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedIllegalArgFirstConst2() throws ParseException {
        Network network = new Network(null,  nodeList.get(1));
    }

}

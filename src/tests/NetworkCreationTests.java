package src.tests;
import org.junit.*;
import src.exceptions.ParseException;
import src.ip.IP;
import src.network.Network;


import java.util.ArrayList;
import java.util.LinkedList;
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
    public void testFirstConstructor() throws ParseException {
        Network network = new Network(root, nodeList.get(1));


    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedIllegalArgFirstConst() throws ParseException {
        Network network = new Network(new IP("0.0.0.0"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void expectedIllegalArgFirstConst2() throws ParseException {
        Network network = new Network(null,  nodeList.get(1));
    }



    @Test
    public void bracketConstructorTest() throws ParseException {
        Network network = new Network("(85.193.148.255 (141.255.1.133 122.117.67.158 0.146.197.108) 34.49.145.239 (231.189.0.127 77.135.84.171 39.20.222.120 252.29.23.0 116.132.83.77))");


    }

    @Test(expected = IllegalArgumentException.class)
    public void nullChildTest() throws ParseException {
        IP ip = null;
        List<IP> list = new LinkedList<>();
        list.add(ip);

        Network network = new Network(new IP("0.0.0.0"), list);
    }
}

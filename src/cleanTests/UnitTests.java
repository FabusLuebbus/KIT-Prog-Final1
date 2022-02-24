package src.cleanTests;

import src.ip.IP;
import src.network.Network;
import src.exceptions.ParseException;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class UnitTests {


    //
    // EXAMPLE INTERACTION TEST
    //

    @Test
    public void exampleInteraction() throws ParseException {
        IP root = new IP("141.255.1.133");
        List<List<IP>> levels = List.of(List.of(root), List.of(new IP("0.146.197.108"), new IP("122.117.67.158")));
        final Network network = new Network(root, levels.get(1));
        Assert.assertEquals("(141.255.1.133 0.146.197.108 122.117.67.158)", network.toString(root));
        Assert.assertTrue((levels.size() - 1) == network.getHeight(root));
        Assert.assertTrue(List.of(List.of(root), levels.get(1)).equals(network.getLevels(root)));
        root = new IP("122.117.67.158");
        levels = List.of(List.of(root), List.of(new IP("141.255.1.133")), List.of(new IP("0.146.197.108")));
        Assert.assertTrue("(122.117.67.158 (141.255.1.133 0.146.197.108))".equals(network.toString(root)));
        Assert.assertTrue((levels.size() - 1) == network.getHeight(root));
        Assert.assertTrue(levels.equals(network.getLevels(root)));
        Assert.assertFalse(network.add(new Network("(122.117.67.158 0.146.197.108)")));
        Assert.assertTrue(network.add(new Network("(85.193.148.81 34.49.145.239 231.189.0.127 141.255.1.133)")));
        Assert.assertTrue((network.add(new Network("(231.189.0.127 252.29.23.0 116.132.83.77 39.20.222.120 77.135.84.171)"))));
        root = new IP("85.193.148.81");
        levels = List.of(List.of(root), List.of(new IP("34.49.145.239"), new IP("141.255.1.133"), new IP("231.189.0.127")), List.of(new IP("0.146.197.108"), new IP("39.20.222.120"), new IP("77.135.84.171"), new IP("116.132.83.77"), new IP("122.117.67.158"), new IP("252.29.23.0")));
        Assert.assertEquals("(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108 122.117.67.158) (231.189.0.127 39.20.222.120 77.135.84.171 116.132.83.77 252.29.23.0))", network.toString(root));
        Assert.assertTrue((levels.size() - 1) == network.getHeight(root));
        Assert.assertTrue(levels.equals(network.getLevels(root)));
        Assert.assertTrue(List.of(new IP("141.255.1.133"), new IP("85.193.148.81"), new IP("231.189.0.127")) .equals(network.getRoute(new IP("141.255.1.133"), new IP("231.189.0.127"))));
        root = new IP("34.49.145.239");
        levels = List.of(List.of(root), List.of(new IP("85.193.148.81")), List.of(new IP("141.255.1.133"), new IP("231.189.0.127")), List.of(new IP("0.146.197.108"), new IP("39.20.222.120"), new IP("77.135.84.171"), new IP("116.132.83.77"), new IP("122.117.67.158"), new IP("252.29.23.0")));
        Assert.assertTrue((levels.size() - 1) == network.getHeight(root));
        Assert.assertTrue(network.disconnect(new IP("85.193.148.81"), new IP("34.49.145.239")));
        Assert.assertEquals(List.of(new IP("0.146.197.108"), new IP("39.20.222.120"), new IP("77.135.84.171"), new IP("85.193.148.81"), new IP("116.132.83.77"), new IP("122.117.67.158"), new IP("141.255.1.133"), new IP("231.189.0.127"), new IP("252.29.23.0")), network.list());
    }



    //
    // IP TESTS
    //


    @Test
    public void parseIPs() throws ParseException {
        IP ip1 = new IP("123.123.123.123");
        Assert.assertEquals("123.123.123.123", ip1.toString());
        IP ip2 = new IP("0.0.0.0");
        Assert.assertEquals("0.0.0.0", ip2.toString());
        IP ip3 = new IP("255.255.255.255");
        Assert.assertEquals("255.255.255.255", ip3.toString());
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP1() throws ParseException {
        IP ip = new IP("123.0.0..");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP2() throws ParseException {
        IP ip = new IP("123.123.123.123.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP3() throws ParseException {
        IP ip = new IP("123.123.123.123 ");
    }
    @Test(expected = ParseException.class)
    public void parseIllegalIP4() throws ParseException {
        IP ip = new IP(" 123.123.123.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP5() throws ParseException {
        IP ip = new IP("123. 123.123.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP6() throws ParseException {
        IP ip = new IP("1.256.123.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP8() throws ParseException {
        IP ip = new IP("1.123..123.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP9() throws ParseException {
        IP ip = new IP("1.123.-1.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP10() throws ParseException {
        IP ip = new IP("355.123.123.123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP11() throws ParseException {
        IP ip = new IP("123123123123");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP12() throws ParseException {
        IP ip = new IP("1.123.1.123;");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP13() throws ParseException {
        IP ip = new IP("1.123.1.a");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP14() throws ParseException {
        IP ip = new IP("1.123.1.123.a");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP15() throws ParseException {
        IP ip = new IP("1.02.1.123.");
    }

    @Test(expected = ParseException.class)
    public void parseIllegalIP16() throws ParseException {
        IP ip = new IP(null);
    }

    @Test
    public void compareIP() throws ParseException {
        IP ip1 = new IP("0.0.0.0");
        IP ip2 = new IP("255.255.255.255");
        IP ip3 = new IP("0.255.0.0");
        IP ip4 = new IP("0.0.123.123");
        IP ip5 = new IP("1.0.0.0");
        IP ip6 = new IP("0.255.255.255");
        IP ip7 = new IP("123.123.123.123");
        IP ip8 = new IP("123.123.123.123");
        IP ip9 = new IP("0.0.1.0");
        IP ip10 = new IP("0.0.2.0");
        Assert.assertEquals(true, ip1.compareTo(ip2) < 0);
        Assert.assertEquals(true, ip2.compareTo(ip3) > 0);
        Assert.assertEquals(true, ip5.compareTo(ip6) > 0);
        Assert.assertEquals(true, ip7.compareTo(ip8) == 0);
        Assert.assertEquals(true, ip2.compareTo(ip2) == 0);
        Assert.assertEquals(true, ip3.compareTo(ip4) > 0);
        Assert.assertEquals(true, ip9.compareTo(ip10) < 0);
    }


    //
    // PARSE EXCEPTION TESTS
    //

    @Test
    public void parseException() {
        Exception e = new ParseException("test");
        Assert.assertEquals("test", e.getMessage());
    }


    //
    // NETWORK TESTS
    //




}

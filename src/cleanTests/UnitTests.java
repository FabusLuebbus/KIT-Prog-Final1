package src.cleanTests;

import src.ip.IP;
import src.network.Network;
import src.exceptions.ParseException;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
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
        Assert.assertTrue(ip4.equals(ip4));
        Assert.assertTrue(ip7.equals(ip8));
        Assert.assertFalse(ip9.equals(ip10));
        Assert.assertFalse(ip1.equals(null));
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


    @Test(expected = RuntimeException.class)
    public void networkConstructorChildrenNull1() throws ParseException {
        IP ip1 = new IP ("1.1.1.1");
        IP ip2 = new IP ("2.2.2.2");
        IP ip3 = new IP ("3.3.3.3");
        IP root = null;
        List<IP> children = List.of(ip2, ip3);
        Network network = new Network(root, children);
    }

    @Test(expected = RuntimeException.class)
    public void networkConstructorChildrenNull2() throws ParseException {
        IP ip1 = new IP ("1.1.1.1");
        IP ip2 = new IP ("2.2.2.2");
        IP ip3 = new IP ("3.3.3.3");
        IP root = ip1;
        List<IP> children = null;
        Network network = new Network(root, children);
    }

    @Test(expected = RuntimeException.class)
    public void networkConstructorChildrenNull3() throws ParseException {
        IP ip1 = new IP ("1.1.1.1");
        IP ip2 = new IP ("2.2.2.2");
        IP ip3 = new IP ("3.3.3.3");
        IP root = ip1;
        List<IP> children = List.of(ip2, null, ip3);
        Network network = new Network(root, children);
    }

    @Test(expected = RuntimeException.class)
    public void networkConstructorChildrenIllegalStructure1() throws ParseException {
        IP ip1 = new IP ("1.1.1.1");
        IP ip11 = new IP ("1.1.1.1");
        IP ip2 = new IP ("2.2.2.2");
        IP ip22 = new IP ("2.2.2.2");
        IP ip3 = new IP ("3.3.3.3");
        IP ip33 = new IP ("3.3.3.3");
        IP root = ip1;
        List<IP> children = List.of(ip2, ip11);
        Network network = new Network(root, children);
    }

    @Test(expected = RuntimeException.class)
    public void networkConstructorChildrenIllegalStructure2() throws ParseException {
        IP ip1 = new IP ("1.1.1.1");
        IP ip11 = new IP ("1.1.1.1");
        IP ip2 = new IP ("2.2.2.2");
        IP ip22 = new IP ("2.2.2.2");
        IP ip3 = new IP ("3.3.3.3");
        IP ip33 = new IP ("3.3.3.3");
        IP root = ip1;
        List<IP> children = List.of(ip2, ip3, ip22);
        Network network = new Network(root, children);
    }


    @Test
    public void networkConstructorChildrenStructure() throws ParseException {
        IP ip1 = new IP ("1.1.1.1");
        IP ip2 = new IP ("2.2.2.2");
        IP ip22 = new IP ("2.2.2.2");
        IP ip3 = new IP ("3.3.3.3");
        IP ip4 = new IP ("4.4.4.4");
        IP root = ip1;
        List<IP> children = List.of(ip2, ip4, ip3);
        Network network = new Network(root, children);
        Assert.assertEquals("(1.1.1.1 2.2.2.2 3.3.3.3 4.4.4.4)", network.toString(ip1));
        Assert.assertEquals("(2.2.2.2 (1.1.1.1 3.3.3.3 4.4.4.4))", network.toString(ip22));
        Assert.assertEquals("(4.4.4.4 (1.1.1.1 2.2.2.2 3.3.3.3))", network.toString(ip4));
    }


    @Test(expected = ParseException.class)
    public void networkConstructorBracketNotationNull() throws ParseException {
        new Network(null);
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation1() throws ParseException {
        new Network("");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation2() throws ParseException {
        new Network(" ");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation3() throws ParseException {
        new Network("()");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation4() throws ParseException {
        new Network("(123.123.123.123)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation5() throws ParseException {
        new Network("(123.123.123.123 12.12.12.12 )");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation6() throws ParseException {
        new Network("(123.123.123.123 12.12.12.12) ");
    }
    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation7() throws ParseException {
        new Network(" (13.123.13.123 12.12.12.12)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation8() throws ParseException {
        new Network("(123.123.123.123  12.12.12.12)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation9() throws ParseException {
        new Network("(123.123.123.123 12.12.12.12");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation10() throws ParseException {
        new Network("(123.123.123.123 12.12.12.12))");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation11() throws ParseException {
        new Network("(123.123.123.123 (12.12.12.12))");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation12() throws ParseException {
        new Network("(132.123.123.123 12.12.12.12)(14.14.14.14 13.13.13.13)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation13() throws ParseException {
        new Network("(123.123.123.123 256.12.12.12)");
    }
    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation14() throws ParseException {
        new Network("(123.123.123.123 12.12..12.12)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation15() throws ParseException {
        new Network("(123.123.123.123a12.12.12.12)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation20() throws ParseException {
        new Network("(123.123.123.123)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation21() throws ParseException {
        new Network("(123.123.123.123 (1.1.1.1 2.2.2.2 (3.3.3.3)))");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation16() throws ParseException {
        new Network("(123.123.123.123 12.12.12.12 123.123.123)");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation17() throws ParseException {
        new Network("(123.123.123.123 (12.12.12.12 13.13.13.13 123.123.123.123))");
    }

    @Test(expected = ParseException.class)
    public void networkConstructorIllegalBracketNotation18() throws ParseException {
        new Network("(123.123.123.123 (12.12.12.12 4.4.4.4) (5.5.5.5 (6.6.6.6 12.12.12.12)))");
    }

    @Test
    public void networkConstructorBracketNotation() throws ParseException {
        Network network = new Network("(3.3.3.3 7.7.7.7 (4.4.4.4 (16.16.16.16 17.17.17.17)) (2.2.2.2 5.5.5.5 1.1.1.1))");
        Assert.assertEquals("(3.3.3.3 (2.2.2.2 1.1.1.1 5.5.5.5) (4.4.4.4 (16.16.16.16 17.17.17.17)) 7.7.7.7)", network.toString(new IP("3.3.3.3")));
        Assert.assertEquals("(5.5.5.5 (2.2.2.2 1.1.1.1 (3.3.3.3 (4.4.4.4 (16.16.16.16 17.17.17.17)) 7.7.7.7)))", network.toString(new IP("5.5.5.5")));
        Assert.assertEquals("(2.2.2.2 1.1.1.1 (3.3.3.3 (4.4.4.4 (16.16.16.16 17.17.17.17)) 7.7.7.7) 5.5.5.5)", network.toString(new IP("2.2.2.2")));
        network = new Network("(2.2.2.2 1.1.1.1)");
        Assert.assertEquals("(2.2.2.2 1.1.1.1)", network.toString(new IP("2.2.2.2")));
        Assert.assertEquals("(1.1.1.1 2.2.2.2)", network.toString(new IP("1.1.1.1")));
    }


    @Test
    public void networkAdd() throws ParseException {
        Network network = new Network("(1.1.1.1 2.2.2.2)");
        Assert.assertFalse(network.add(null));
        Assert.assertFalse(network.add(new Network("(1.1.1.1 2.2.2.2)")));
        Assert.assertTrue(network.add(new Network("(1.1.1.1 3.3.3.3 2.2.2.2)")));
        Assert.assertFalse(network.add(network));
        Assert.assertFalse(network.add(new Network("(2.2.2.2 3.3.3.3)")));
        Assert.assertEquals("(3.3.3.3 (1.1.1.1 2.2.2.2))", network.toString(new IP("3.3.3.3")));
        Assert.assertTrue(network.add(new Network("(5.5.5.5 6.6.6.6)")));
        Assert.assertEquals("(6.6.6.6 5.5.5.5)", network.toString(new IP("6.6.6.6")));
    }

    @Test
    public void networkList() throws ParseException{
        Network network = new Network("(2.2.2.2 4.4.4.4 (3.3.3.3 5.5.5.5))");
        network.add(new Network("(1.1.1.1 6.6.6.6)"));
        Assert.assertEquals(List.of(new IP("1.1.1.1"), new IP("2.2.2.2"), new IP("3.3.3.3"), new IP("4.4.4.4"), new IP("5.5.5.5"), new IP("6.6.6.6")), network.list());
    }

    @Test
    public void networkConnect() throws ParseException {
        Network network = new Network("(1.1.1.1 (2.2.2.2 3.3.3.3) 4.4.4.4)");
        Assert.assertFalse(network.connect(null, new IP("1.1.1.1")));
        Assert.assertFalse(network.connect(new IP("1.1.1.1"), new IP("8.8.8.8")));
        Assert.assertFalse(network.connect(new IP("1.1.1.1"), new IP("4.4.4.4")));
        Assert.assertFalse(network.connect(new IP("4.4.4.4"), new IP("3.3.3.3")));
        network.add(new Network("(5.5.5.5 6.6.6.6)"));
        Assert.assertTrue(network.connect(new IP("2.2.2.2"), new IP("6.6.6.6")));
        Assert.assertEquals("(1.1.1.1 (2.2.2.2 3.3.3.3 (6.6.6.6 5.5.5.5)) 4.4.4.4)", network.toString(new IP("1.1.1.1")));
    }

    @Test
    public void networkDisconnect() throws ParseException {
        Network network = new Network("(1.1.1.1 2.2.2.2 3.3.3.3)");
        Assert.assertFalse(network.disconnect(new IP("1.1.1.1"), null));
        Assert.assertFalse(network.disconnect(new IP("5.5.5.5"), new IP("1.1.1.1")));
        Assert.assertFalse(network.disconnect(new IP("3.3.3.3"), new IP("3.3.3.3")));
        Assert.assertTrue(network.disconnect(new IP("3.3.3.3"), new IP("1.1.1.1")));
        Assert.assertFalse(network.contains(new IP("3.3.3.3")));
        Assert.assertTrue(network.contains(new IP("1.1.1.1")));
        Assert.assertEquals("(2.2.2.2 1.1.1.1)", network.toString(new IP("2.2.2.2")));
        Assert.assertFalse(network.disconnect(new IP("2.2.2.2"), new IP("1.1.1.1")));
        Assert.assertEquals("(2.2.2.2 1.1.1.1)", network.toString(new IP("2.2.2.2")));
        Assert.assertFalse(network.contains(new IP("3.3.3.3")));
        network.add(new Network("(3.3.3.3 4.4.4.4)"));
        Assert.assertFalse(network.disconnect(new IP("3.3.3.3"), new IP("2.2.2.2")));
        Assert.assertTrue(network.disconnect(new IP("3.3.3.3"), new IP("4.4.4.4")));
        Assert.assertFalse(network.contains(new IP("3.3.3.3")));
        Assert.assertFalse(network.contains(new IP("4.4.4.4")));
    }


    @Test
    public void networkContains() throws ParseException {
        Network network = new Network("(2.2.2.2 4.4.4.4 (3.3.3.3 5.5.5.5))");
        network.add(new Network("(1.1.1.1 6.6.6.6)"));
        Assert.assertFalse(network.contains(null));
        Assert.assertTrue(network.contains(new IP("3.3.3.3")));
        Assert.assertTrue(network.contains(new IP("2.2.2.2")));
        Assert.assertTrue(network.contains(new IP("1.1.1.1")));
        Assert.assertFalse(network.contains(new IP("2.2.4.4")));
    }


    @Test
    public void networkHeight() throws ParseException {
        Network network = new Network("(2.2.2.2 4.4.4.4 (3.3.3.3 5.5.5.5))");
        network.add(new Network("(1.1.1.1 6.6.6.6)"));
        Assert.assertEquals(0, network.getHeight(null));
        Assert.assertEquals(0, network.getHeight(new IP("123.123.123.123")));
        Assert.assertEquals(1, network.getHeight(new IP("6.6.6.6")));
        Assert.assertEquals(2, network.getHeight(new IP("3.3.3.3")));
        Assert.assertEquals(3, network.getHeight(new IP("5.5.5.5")));
        Assert.assertEquals(2, network.getHeight(new IP("2.2.2.2")));
        Assert.assertEquals(3, network.getHeight(new IP("4.4.4.4")));
    }

    @Test
    public void networkLevels() throws ParseException {
        Network network = new Network("(5.5.5.5 7.7.7.7 (3.3.3.3 6.6.6.6) (2.2.2.2 1.1.1.1))");
        network.add(new Network("(12.12.12.12 13.13.13.13)"));
        Assert.assertEquals(new ArrayList<IP>(), network.getLevels(null));
        Assert.assertEquals(new ArrayList<IP>(), network.getLevels(new IP("123.123.123.123")));
        Assert.assertEquals(List.of(List.of(new IP("5.5.5.5")), List.of(new IP("2.2.2.2"), new IP("3.3.3.3"), new IP("7.7.7.7")), List.of(new IP("1.1.1.1"), new IP("6.6.6.6"))), network.getLevels(new IP("5.5.5.5")));
        Assert.assertEquals(List.of(List.of(new IP("3.3.3.3")), List.of(new IP("5.5.5.5"), new IP("6.6.6.6")), List.of(new IP("2.2.2.2"), new IP("7.7.7.7")), List.of(new IP("1.1.1.1"))), network.getLevels(new IP("3.3.3.3")));
    }

    @Test
    public void networkRoute() throws ParseException {
        Network network = new Network("(1.1.1.1 2.2.2.2 (3.3.3.3 4.4.4.4 5.5.5.5))");
        network.add(new Network("(8.8.8.8 9.9.9.9)"));
        Assert.assertEquals(new LinkedList<>(), network.getRoute(new IP("1.1.1.1"), null));
        Assert.assertEquals(new LinkedList<>(), network.getRoute(null, new IP("1.1.1.1")));
        Assert.assertEquals(new LinkedList<>(), network.getRoute(new IP("75.75.75.75"), new IP("1.1.1.1")));
        Assert.assertEquals(new LinkedList<>(), network.getRoute(new IP("1.1.1.1"), new IP("75.75.75.75")));
        Assert.assertEquals(List.of(new IP("4.4.4.4"), new IP("3.3.3.3"), new IP("1.1.1.1"), new IP("2.2.2.2")), network.getRoute(new IP("4.4.4.4"), new IP("2.2.2.2")));
        Assert.assertEquals(new LinkedList<>(), network.getRoute(new IP("2.2.2.2"), new IP("2.2.2.2")));
        Assert.assertEquals(new LinkedList<>(), network.getRoute(new IP("2.2.2.2"), new IP("8.8.8.8")));
    }

    @Test
    public void networkToString() throws ParseException {
        Network network = new Network("(1.1.1.1 2.2.2.2)");
        Assert.assertEquals("", network.toString(null));
        Assert.assertEquals("", network.toString(new IP("3.3.3.3")));
    }

}

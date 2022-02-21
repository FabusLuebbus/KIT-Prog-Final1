package src.cleanTests;

import org.junit.Test;
import src.exceptions.ParseException;
import src.ip.IP;
import src.network.Network;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ExampleInteractions {
    public static void main(String[] args) throws ParseException {
         // Construct initial network
        IP root = new IP("141.255.1.133");
         List<List<IP>> levels = List.of(List.of(root),
                 List.of(new IP("0.146.197.108"), new IP("122.117.67.158")));
         final Network network = new Network(root, levels.get(1));
         // (141.255.1.133 0.146.197.108 122.117.67.158)
         System.out.println(network.toString(root));
         // true
         System.out.println((levels.size() - 1) == network.getHeight(root));
         // true
         System.out.println(List.of(List.of(root), levels.get(1))
         .equals(network.getLevels(root)));

         // "Change" root and call toString, getHeight and getLevels again
         root = new IP("122.117.67.158");
         levels = List.of(List.of(root), List.of(new IP("141.255.1.133")),
         List.of(new IP("0.146.197.108")));
         // true
         System.out.println("(122.117.67.158 (141.255.1.133 0.146.197.108))"
                 .equals(network.toString(root)));
         // true
         System.out.println((levels.size() - 1) == network.getHeight(root));
         // true
         System.out.println(levels.equals(network.getLevels(root)));

         // Try to add circular dependency
         // false
         System.out.println(
                 network.add(new Network("(122.117.67.158 0.146.197.108)")));

         // Merge two subnets with initial network
         // true
         System.out.println(network.add(new Network(
                 "(85.193.148.81 34.49.145.239 231.189.0.127 141.255.1.133)")));
         // true
         System.out
         .println(network.add(new Network("(231.189.0.127 252.29.23.0"
                 + " 116.132.83.77 39.20.222.120 77.135.84.171)")));
         // "Change" root and call toString, getHeight and getLevels again
         root = new IP("85.193.148.81");
         levels = List.of(List.of(root),
                 List.of(new IP("34.49.145.239"), new IP("141.255.1.133"),
                         new IP("231.189.0.127")),
         List.of(new IP("0.146.197.108"), new IP("39.20.222.120"),
                 new IP("77.135.84.171"), new IP("116.132.83.77"),
                 new IP("122.117.67.158"), new IP("252.29.23.0")));
         // true
         System.out.println(

        ("(85.193.148.81 34.49.145.239 (141.255.1.133 0.146.197.108"
         + " 122.117.67.158) (231.189.0.127 39.20.222.120"
         + " 77.135.84.171 116.132.83.77 252.29.23.0))").equals(network.toString(root)));
         // true
         System.out.println((levels.size() - 1) == network.getHeight(root));
         // true
         System.out.println(levels.equals(network.getLevels(root)));
         // true
         System.out.println(List
                 .of(new IP("141.255.1.133"), new IP("85.193.148.81"),
                         new IP("231.189.0.127"))
         .equals(network.getRoute(new IP("141.255.1.133"),
                 new IP("231.189.0.127"))));

         // "Change" root and call getHeight again
         root = new IP("34.49.145.239");
         levels = List.of(List.of(root), List.of(new IP("85.193.148.81")),
                 List.of(new IP("141.255.1.133"), new IP("231.189.0.127")),
                 List.of(new IP("0.146.197.108"), new IP("39.20.222.120"),
                         new IP("77.135.84.171"), new IP("116.132.83.77"),
                 new IP("122.117.67.158"), new IP("252.29.23.0")));
         // true
         System.out.println((levels.size() - 1) == network.getHeight(root));
         // Remove edge and list tree afterwards
         // true
         System.out.println(network.disconnect(new IP("85.193.148.81"),
                 new IP("34.49.145.239")));
         // true
         System.out.println(
                 List.of(new IP("0.146.197.108"), new IP("39.20.222.120"),
                         new IP("77.135.84.171"), new IP("85.193.148.81"),
                 new IP("116.132.83.77"), new IP("122.117.67.158"),
                 new IP("141.255.1.133"), new IP("231.189.0.127"),
                 new IP("252.29.23.0")).equals(network.list()));
         }

 public static class IPTest {
     IP ip1 = new IP("0.0.0.0");
     IP ip11 = new IP("0.0.0.0");
     IP ip2 = new IP("211.1.1.1");
     IP ip22 = new IP("211.1.1.1");
     IP ip3 = new IP("255.255.255.255");
     IP ip33 = new IP("255.255.255.255");
     IP ip4 = new IP("54.97.43.123");

     public IPTest() throws ParseException {
     }

     @Test(expected = ParseException.class)
     public void errorTest() throws ParseException {
         IP differentIP = new IP("2111.1.1.1");
     }

     @Test
     public void constructorTest() throws ParseException {
         assertEquals(3540058369L, ip2.getIpValue());
     }

     @Test
     public void comparisonTest() throws ParseException {
         assertEquals(-1, ip1.compareTo(ip2));
         assertEquals(-1, ip1.compareTo(ip3));
         assertEquals(0, ip1.compareTo(ip11));
         assertEquals(1, ip2.compareTo(ip1));
         assertEquals(-1, ip2.compareTo(ip3));
         assertEquals(0, ip2.compareTo(ip22));
         assertEquals(1, ip3.compareTo(ip1));
         assertEquals(1, ip3.compareTo(ip2));
         assertEquals(0, ip3.compareTo(ip33));
     }

     @Test
     public void equalsTest() throws ParseException {
         Object o = new Object();
         assertEquals(ip2, ip22);
         assertNotEquals(ip2, ip3);
         assertNotEquals(ip2, o);
     }

     @Test
     public void toStringTest() throws ParseException {
         assertEquals("0.0.0.0", ip1.toString());
         assertEquals("0.255.0.255", new IP("0.255.0.255").toString());
         assertEquals("211.1.1.1", ip2.toString());
         assertEquals("255.255.255.255", ip3.toString());
         assertEquals("54.97.43.123", ip4.toString());
     }
 }
}

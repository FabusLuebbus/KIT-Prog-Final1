package src.ip;

import src.exceptions.ParseException;
import org.junit.*;

import static org.junit.Assert.*;

public class IPTest {
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
        assertEquals("11010011000000010000000100000001", ip2.getIpAsBinary());
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
        assertEquals("211.1.1.1", ip2.toString());
        assertEquals("255.255.255.255", ip3.toString());
        assertEquals("54.97.43.123", ip4.toString());
    }
}

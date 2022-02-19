package src.parsing;
import org.junit.*;
import src.exceptions.ParseException;
import src.parsing.BracketNotationParser;

import java.io.IOException;

public class AlternateParserTests {
    @Test
    public void parserTest1() throws IOException, ParseException {
        BracketNotationParser parser = new BracketNotationParser();
        parser.parse("(0.0.0.0 (1.1.1.1 2.2.2.2) 3.3.3.3)");
        parser.parse("(0.0.0.0)");
    }
}

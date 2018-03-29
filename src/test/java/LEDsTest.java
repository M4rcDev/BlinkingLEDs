import de.marcmaurer.Main;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class LEDsTest {

    @Test
    public void ldA14() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main ledAssembly = new Main();
        ledAssembly.processCommandString("ld a,14");
        ledAssembly.processCommandString("out (0),a");
        assertEquals("....***." + System.lineSeparator(), outContent.toString());

    }

    @Test
    public void twoLEDs() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Main ledAssembly = new Main();
        ledAssembly.processCommandString("ld a,14");
        ledAssembly.processCommandString("out (0),a");
        ledAssembly.processCommandString("ld a,12");
        ledAssembly.processCommandString("out (0),a");
        assertEquals("....***." + System.lineSeparator() + "....**.." + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void getBitTest() {
        Main ledAssembly = new Main();

        assertEquals(1, ledAssembly.getBit(2,14 ));
        assertEquals(1, ledAssembly.getBit(2,12 ));
        assertEquals(0, ledAssembly.getBit(1,1 ));

    }


}
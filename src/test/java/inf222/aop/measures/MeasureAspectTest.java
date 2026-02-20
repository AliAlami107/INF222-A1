package inf222.aop.measures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MeasureAspectTest {

    private Measures measures;
    private PrintStream standardOut;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        measures = new Measures();

        standardOut = System.out;
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void cleanUp() {
        outputStreamCaptor.reset();
        System.setOut(standardOut);
    }

    @Test
    @DisplayName("Test that example run is as expected")
    void testExampleRun() {
        measures.runExample();
        assertEquals("""
                Initial Values:
                l_ft: 1.524
                w_in: 1.0922
                h_cm: 1.3
                a_m: 0.7
                b_yd: 1.4630400000000001

                After First Operations:
                l_ft: 1.6002
                w_in: 2.4922
                h_cm: 0.30000000000000004
                a_m: 0.35
                b_yd: 2.9630400000000003

                Computed Values:
                Volume of box: 1.1964055320000002
                Sum of all distances: 7.705439999999999
                Average value: 1.5410879999999998
                Difference (ft - m): 1.2502""".trim(), outputStreamCaptor.toString().trim());
    }

    @Test
    @DisplayName("Test that assigning negative values fails")
    void testNegativeValues() {
        Error error = assertThrows(Error.class, () -> {
            measures.assignNegativeValue();
        });

        assertEquals("Illegal modification", error.getMessage());
    }
}

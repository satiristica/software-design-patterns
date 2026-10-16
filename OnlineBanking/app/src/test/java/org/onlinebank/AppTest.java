package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ResourceLock("java.lang.System.in")
@ResourceLock(Resources.SYSTEM_OUT)
class AppTest {

    @Test
    void opensBothMenusAndReturnsToMainMenu() {
        String output = runApp("1\n0\n2\n0\n0\n");

        assertTrue(output.contains("1. New transfer"));
        assertTrue(output.contains("1. Account balance"));
        assertFalse(output.contains("Operation failed:"));
    }

    @Test
    void handlesUnknownOptionsAndInvalidAmount() {
        String output = runApp(
                "9\n1\n9\n2\n9\n1\n1\nACC-001\nabc\n0\n"
        );

        assertTrue(output.contains("Unknown option."));
        assertTrue(output.contains("Operation failed: Unknown option"));
        assertTrue(output.contains("Amount in account currency:"));

        int errors = output.split("Operation failed:", -1).length - 1;

        assertEquals(3, errors);
    }

    @Test
    void handlesEndOfInput() {
        assertTrue(
                runApp("").contains("Online Banking")
        );

        assertTrue(
                runApp("1\n").contains(
                        "Operation failed: No input provided"
                )
        );
    }

    private String runApp(String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream captured = new PrintStream(
                output, true, StandardCharsets.UTF_8
        )) {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)
            ));

            System.setOut(captured);

            App.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
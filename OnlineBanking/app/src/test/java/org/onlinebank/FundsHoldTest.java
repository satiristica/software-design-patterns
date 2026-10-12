package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.intake.FundsHold;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FundsHoldTest {

    
    @Test
    void newHoldHasCorrectDetails() {
        FundsHold hold = holdWithAmount("100000");

        assertEquals("TR-001", hold.getTransferId());
        assertEquals("ACC-001", hold.getAccountId());
        assertEquals(new BigDecimal("100000"), hold.getAmount());
        assertEquals(FundsHold.Status.NEW, hold.getStatus());
    }

    
    @Test
    void placeReservesMoney() {
        FundsHold hold = holdWithAmount("100000");

        hold.place();

        assertEquals(FundsHold.Status.HELD, hold.getStatus());
    }


    @Test
    void captureConfirmsHeldMoney() {
        FundsHold hold = holdWithAmount("100000");
        hold.place();

        hold.capture();

        assertEquals(FundsHold.Status.CAPTURED, hold.getStatus());
    }


    @Test
    void releaseCancelsHeldMoney() {
        FundsHold hold = holdWithAmount("100000");
        hold.place();

        hold.release();

        assertEquals(FundsHold.Status.RELEASED, hold.getStatus());
    }


    @Test
    void cannotCaptureBeforePlacing() {
        FundsHold hold = holdWithAmount("100000");

        assertThrows(IllegalStateException.class, hold::capture);
        assertEquals(FundsHold.Status.NEW, hold.getStatus());
    }


    @Test
    void cannotReleaseBeforePlacing() {
        FundsHold hold = holdWithAmount("100000");

        assertThrows(IllegalStateException.class, hold::release);
        assertEquals(FundsHold.Status.NEW, hold.getStatus());
    }


    @Test
    void cannotReleaseTwice() {
        FundsHold hold = holdWithAmount("100000");
        hold.place();
        hold.release();

        assertThrows(IllegalStateException.class, hold::release);
        assertEquals(FundsHold.Status.RELEASED, hold.getStatus());
    }


    @Test
    void cannotReleaseCapturedMoney() {
        FundsHold hold = holdWithAmount("100000");
        hold.place();
        hold.capture();

        assertThrows(IllegalStateException.class, hold::release);
        assertEquals(FundsHold.Status.CAPTURED, hold.getStatus());
    }


    @Test
    void cannotCreateHoldWithZeroAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> holdWithAmount("0")
        );
    }


    @Test
    void cannotCreateHoldWithNegativeAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> holdWithAmount("-100")
        );
    }


    private FundsHold holdWithAmount(String amount) {
        return new FundsHold(
                "TR-001",
                "ACC-001",
                new BigDecimal(amount)
        );
    }
}
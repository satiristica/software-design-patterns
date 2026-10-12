package org.onlinebank;

import org.junit.jupiter.api.Test;
import org.onlinebank.intake.CurrencyExchange;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyExchangeTest {

    private final CurrencyExchange exchange = new CurrencyExchange();

    @Test
    void convertsKztToUsd() {
        BigDecimal result = exchange.convert(
                new BigDecimal("90000"), "KZT", "USD"
        );

        assertEquals(new BigDecimal("200"), result);
    }

    @Test
    void convertsUsdToKzt() {
        BigDecimal result = exchange.convert(
                new BigDecimal("200"), "USD", "KZT"
        );

        assertEquals(new BigDecimal("90000"), result);
    }

    @Test
    void sameCurrencyKeepsAmount() {
        BigDecimal result = exchange.convert(
                new BigDecimal("1000"), "KZT", "KZT"
        );

        assertEquals(new BigDecimal("1000"), result);
    }

    @Test
    void rejectsUnsupportedCurrency() {
        assertThrows(
                IllegalArgumentException.class,
                () -> exchange.convert(
                        new BigDecimal("1000"), "KZT", "EUR"
                )
        );
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> exchange.convert(
                        BigDecimal.ZERO, "KZT", "USD"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> exchange.convert(
                        new BigDecimal("-100"), "KZT", "USD"
                )
        );
    }
}
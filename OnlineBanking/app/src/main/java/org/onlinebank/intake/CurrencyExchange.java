package org.onlinebank.intake;

import java.math.BigDecimal;

public class CurrencyExchange {

    private static final BigDecimal KZT_PER_USD = new BigDecimal("450");

    public BigDecimal convert(BigDecimal amount, String sourceCurrency, String targetCurrency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }


        if (!isSupported(sourceCurrency) || !isSupported(targetCurrency)) {
            throw new IllegalArgumentException("Unsupported currency");
        }

        
        if (sourceCurrency.equals(targetCurrency)) {
            return amount;
        }


        if (sourceCurrency.equals("KZT")) {
            return amount.divide(KZT_PER_USD);
        }


        return amount.multiply(KZT_PER_USD);
    }


    private boolean isSupported(String currency) {
        return "KZT".equals(currency) || "USD".equals(currency);
    }
}
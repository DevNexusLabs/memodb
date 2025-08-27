package com.spring.memodb.resp;


import java.util.Arrays;

public enum FirstByte {
    STRING("+"),
    BULK_STRING("$"),
    ARRAY("*");

    private final String symbol;

    FirstByte(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static FirstByte fromSymbol(String symbol) {
        return Arrays.stream(FirstByte.values())
                .filter(type -> type.symbol.equals(symbol))
                .findFirst()
                .orElse(null);
    }
}

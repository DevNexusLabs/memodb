package com.spring.memodb.resp;

import java.util.List;
import java.util.stream.Collectors;

public record RespType(
        FirstByte dataType,
        String string,
        int number,
        boolean bool,
        List<RespType> array) {

    public String serializeResp() {
        return switch (dataType) {
            case STRING -> "+" + string + "\r\n";
            case BULK_STRING -> "$" + string.length() + "\r\n" + string + "\r\n";
            case ARRAY -> serializeArray();
        };
    }



    private String serializeArray() {
        if (array == null || array.isEmpty()) {
            return "*0\r\n";
        }
        String body = array.stream()
                .map(RespType::serializeResp)
                .collect(Collectors.joining());

        return "*" + array.size() + "\r\n" + body;
    }
}

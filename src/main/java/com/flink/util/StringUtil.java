package com.flink.util;

public class StringUtil {

    public static String toStringNotNull(Object var) {
        if (var == null) {
            return "";
        }
        return var.toString();
    }

}

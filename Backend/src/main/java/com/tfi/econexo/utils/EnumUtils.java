package com.tfi.econexo.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnumUtils {

    public static <T extends Enum<T>>List<Map<String, String>> toDropdownList(Class<T> enumClass){
        return Arrays.stream(enumClass.getEnumConstants())
                .map(type -> {
                    String name = type.name();
                    String label = name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");

                    return Map.of(
                            "value", name,
                            "label", label
                    );
                }).toList();
    }
}

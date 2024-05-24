package com.yhh.protocol;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hyh
 * @date 2024/5/23
 */
@Getter
public enum ProtocolMessageSerializerEnum {
    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");

    private int key;
    private String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }



    /**
     * 获取值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());

        /*
         ProtocolMessageSerializerEnum[] values = values();
        该代码片段是Java中的一个函数调用，函数名为values()，它返回一个ProtocolMessageSerializerEnum类型的数组。这个函数是Enum类的一个公共静态方法，用于返回该枚举类型的所有值。
        该函数属于Enum类，用于获取枚举类型的所有值。
        函数没有参数。
        返回值是一个ProtocolMessageSerializerEnum类型的数组，其中包含了该枚举类型的所有值
         */
    }

    /**
     * 根据key获取列表
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据value获取枚举
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
        if (StrUtil.isBlank(value)) return null;
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }


}

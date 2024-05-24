package com.yhh.protocol;

import lombok.Getter;

/**
 * @author hyh
 * @date 2024/5/23
 */
@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);

    private final String text;
    private final int value;

    ProtocolMessageStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum enumItem : ProtocolMessageStatusEnum.values()) {
            if (value == enumItem.getValue()) {
                return enumItem;
            }
        }
        return null;
    }
}

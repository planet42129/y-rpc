package com.yhh.protocol;

import lombok.Getter;

/**
 * @author hyh
 * @date 2024/5/23
 */
@Getter
public enum ProtocolMessageTypeEnum {
    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHERS(3);


    private int key;

    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }

    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum enumItem : ProtocolMessageTypeEnum.values()) {
            if (key == enumItem.getKey()) {
                return enumItem;
            }
        }
        return null;
    }
}

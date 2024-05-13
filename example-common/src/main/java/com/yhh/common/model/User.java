package com.yhh.common.model;

import java.io.Serializable;

/**
 * @author hyh
 * @date 2024/5/11
 */
public class User implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

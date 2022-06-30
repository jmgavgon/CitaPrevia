package com.jmg.citaprevia.model;

import com.jmg.citaprevia.ProviderType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class User implements Serializable {

    private String email;
    private String name;
    private ProviderType providerType;

    public User() {
    }

    public User(String email, String name, ProviderType providerType) {
        this.email = email;
        this.name = name;
        this.providerType = providerType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }
}

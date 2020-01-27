package com.example.keyservicetestapp;

import java.security.interfaces.RSAPublicKey;

public class Person {
    public String name;
    private RSAPublicKey publicKey;

    public Person(String name){
        this.name = name;
    }
}

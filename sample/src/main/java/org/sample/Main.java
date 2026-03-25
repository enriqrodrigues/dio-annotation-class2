package org.sample;

import  org.sample.model.PersonBuilder;

public class Main {
    public static void main(String[] args) {
        var person = new PersonBuilder()
                .id(1)
                .name("John Doe")
                .build();
        System.out.println(person);
    }
}
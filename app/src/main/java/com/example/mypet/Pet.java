package com.example.mypet;

public class Pet {

    private String pet_mode;
    private String pet_name;
    private long pet_age;

    public Pet(){}

    public Pet(String pet_mode, String pet_name, long pet_age) {
        this.pet_mode = pet_mode;
        this.pet_name = pet_name;
        this.pet_age = pet_age;
    }

    public void setPetMode(String mode) {
        this.pet_mode = mode;
    }

    public void setPetName(String name) {
        this.pet_name = name;
    }

    public void setPetAge(long age) {
        this.pet_age = age;
    }

    public String getPetMode() {
        return pet_mode;
    }

    public String getPetName() {
        return pet_name;
    }

    public long getPetAge() {
        return pet_age;
    }
}

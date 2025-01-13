package com.example.personel_takip.model;

import java.io.Serializable;

public class Person implements Serializable {

    public String id, name, surname, birtDate,department, phone, address, date, image;
    public boolean gender;

    public Person(){

    }

    public Person( String id, String name, String surname, String birtDate,String department, boolean gender, String phone, String address, String date, String image) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birtDate = birtDate;
        this.department = department;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.date = date;
        this.image = image;
    }


}

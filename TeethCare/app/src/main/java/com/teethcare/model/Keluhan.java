package com.teethcare.model;

public class Keluhan {
    public String deksripsiKeluhan, kataKunci, avatar;

    public Keluhan(){};

    public Keluhan(String adddeksripsiKeluhan, String addkataKunci, String imgavatar){
        this.deksripsiKeluhan  = adddeksripsiKeluhan;
        this.kataKunci = addkataKunci;
        this.avatar = imgavatar;
    }
}

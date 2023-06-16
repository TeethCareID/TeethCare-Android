package com.teethcare.model;

public class javaDataDiri {
    public String id, nama, noHp, doB, jenisKelamin, alamat;

    public javaDataDiri(){};

    public javaDataDiri(String textDoB, String textjenisKelamin, String textPhone, String textAlamat){
        this.doB = textDoB;
        this.jenisKelamin = textjenisKelamin;
        this.noHp = textPhone;
        this.alamat = textAlamat;
    }

}

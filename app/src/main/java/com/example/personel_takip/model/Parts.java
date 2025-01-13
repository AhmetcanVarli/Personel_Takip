package com.example.personel_takip.model;

public class Parts {
    public String barcodeNo, partsName;
    public Double partsPrice;
    public int partsAmount;

    public Parts(){

    }

    public Parts(String barcodeNo, String partsName, double partsPrice, int partsAmount) {
        this.barcodeNo = barcodeNo;
        this.partsName = partsName;
        this.partsPrice = partsPrice;
        this.partsAmount = partsAmount;
    }
}

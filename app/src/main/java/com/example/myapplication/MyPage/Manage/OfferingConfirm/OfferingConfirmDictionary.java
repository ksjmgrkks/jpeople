package com.example.myapplication.MyPage.Manage.OfferingConfirm;

public class OfferingConfirmDictionary {
    private String offeringSort;
    private String offeringMoney;
    private String offeringDate;
    private String offeringContents;
    public String getOfferingContents() {
        return offeringContents;
    }

    public void setOfferingContents(String offeringContents) {
        this.offeringContents = offeringContents;
    }
    public String getOfferingSort() {
        return offeringSort;
    }

    public void setOfferingSort(String offeringSort) {
        this.offeringSort = offeringSort;
    }

    public String getOfferingMoney() {
        return offeringMoney;
    }

    public void setOfferingMoney(String offeringMoney) {
        this.offeringMoney = offeringMoney;
    }

    public String getOfferingDate() {
        return offeringDate;
    }

    public void setOfferingDate(String offeringDate) {
        this.offeringDate = offeringDate;
    }

    public OfferingConfirmDictionary(String offeringSort, String offeringMoney, String offeringDate, String offeringContents) {
        this.offeringSort = offeringSort;
        this.offeringMoney = offeringMoney;
        this.offeringDate = offeringDate;
        this.offeringContents = offeringContents;
    }


}

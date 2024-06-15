package com.dashan.p2p.model.vo;

import java.io.Serializable;

// Serializable序列化
public class BidUserVO implements Serializable {

    private String phone;
    private Double bidMoney;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }
}

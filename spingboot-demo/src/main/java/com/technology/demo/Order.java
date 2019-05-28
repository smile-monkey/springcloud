package com.technology.demo;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    public Order() {
    }

    public Order(String orderNo, Date createTime) {
        this.orderNo = orderNo;
        this.createTime = createTime;
    }

    private String orderNo;
    private Date createTime;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNo='" + orderNo + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}

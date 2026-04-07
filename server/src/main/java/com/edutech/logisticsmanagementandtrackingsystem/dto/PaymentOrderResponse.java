package com.edutech.logisticsmanagementandtrackingsystem.dto;

public class PaymentOrderResponse {

    private String orderId;
    private long amount; // in paise
    private String currency;
    private String razorpayKey;
    private Long paymentRecordId;

    public PaymentOrderResponse() {}

    public PaymentOrderResponse(String orderId, long amount, String currency, String razorpayKey, Long paymentRecordId) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.razorpayKey = razorpayKey;
        this.paymentRecordId = paymentRecordId;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRazorpayKey() { return razorpayKey; }
    public void setRazorpayKey(String razorpayKey) { this.razorpayKey = razorpayKey; }

    public Long getPaymentRecordId() { return paymentRecordId; }
    public void setPaymentRecordId(Long paymentRecordId) { this.paymentRecordId = paymentRecordId; }
}
package com.example.newdemo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Getter
public class Finances {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private State state;

    @ManyToOne
    private City city;

    @ManyToOne
    private Phrases phrases;

    @ManyToOne
    private Users owner;

    private Property.PropertyType type;

    private double price;

    private String paidBy;

    private LocalDate date;

    private LocalDateTime dateTime;

    private double amountPaid;

    private double outstandingAmount;

    public Finances(){}

    public Finances(Long id, State state, City city, Phrases phrases,
                    Users owner, Property.PropertyType type,
                    double price, String paidBy,
                    LocalDate date, double amountPaid,
                    double outstandingAmount, LocalDateTime dateTime) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.phrases = phrases;
        this.owner = owner;
        this.type = type;
        this.price = price;
        this.paidBy = paidBy;
        this.date = date;
        this.amountPaid = amountPaid;
        this.outstandingAmount = outstandingAmount;
        this.dateTime = dateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setState(State state) {this.state = state;}

    public void setCity(City city) {this.city = city;}

    public void setPhrases(Phrases phrases) {this.phrases = phrases;}

    public void setOwner(Users owner) {
        this.owner = owner;
    }

    public void setType(Property.PropertyType type) {
        this.type = type;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getAmountPaidFormattedToString(){
        String nairaSymbol = "\u20A6";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedAmount = decimalFormat.format(this.amountPaid);
        return nairaSymbol + formattedAmount;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public void updateOutstandingAmount() {
        outstandingAmount = this.price - this.amountPaid;
    }

    public String getOutstandingFormattedToString(){
        String nairaSymbol = "\u20A6";
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        updateOutstandingAmount();
        String formattedAmount = decimalFormat.format(outstandingAmount);
        return nairaSymbol + formattedAmount;
    }


}

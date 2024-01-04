package com.example.newdemo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class Property {

    public enum PropertyFeatures {
        SwimmingPool("Swimming Pool"), Garden("Garden"), Garage("Garage");

        private String name;

        PropertyFeatures(String name){
            this.name = name;
        }
    }

    public enum PropertyServices {
        Water("Water"), Electricity("Elctricity"), Gas("Gas"), Sewage("Sewage");
        private String name;

        PropertyServices(String name){
            this.name = name;
        }
    }

    public enum PropertyStatus {
        Available("Available"), UnderOffer("Under Offer"), Sold("Sold");
        private String name;

        PropertyStatus(String name){
            this.name = name;
        }
    }

    public enum PropertyType {
        Land("Land"), SemiDetachedDuplex("Semi Detached Duplex"), DetachedDuplex("Detached Duplex"), Bungalow("Bungalow");

        private String name;
        PropertyType(String name){
            this.name = name;
        }

    }

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne
    private State state;

    @NotNull
    @ManyToOne
    private City city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Property.PropertyType type;

    private int lotSize;

    private int noOfBedrooms;

    private int noOfBathrooms;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Property.PropertyStatus status;

    @Enumerated(EnumType.STRING)
    private Set<PropertyServices> services;

    @Enumerated(EnumType.STRING)
    private Set<Property.PropertyFeatures> features;

    private String owners;


    private String description;

    @Column(nullable = false)
    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER)
    private List<PropertyImage> propertyImages = new ArrayList<>();

    public Property() {
    }

    public Property(Long id, State state, City city, String street,
                    PropertyType type, int lotSize, double price, PropertyStatus status,
                    String description, List<PropertyImage> propertyImages) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.street = street;
        this.type = type;
        this.lotSize = lotSize;
        this.price = price;
        this.status = status;
        this.description = description;
        this.propertyImages = propertyImages;
    }

    public Property(Long id, State state, City city, String street,
                    PropertyType type, int lotSize, double price, PropertyStatus status,
                    String owners, String description, List<PropertyImage> propertyImages) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.street = street;
        this.type = type;
        this.lotSize = lotSize;
        this.price = price;
        this.status = status;
        this.owners = owners;
        this.description = description;
        this.propertyImages = propertyImages;
    }
    public Property(Long id, State state, City city, String street,
                    PropertyType type, int lotSize, int noOfBedrooms,
                    int noOfBathrooms, double price, PropertyStatus status,
                    Set<PropertyServices> services, Set<PropertyFeatures> features,
                    String description, List<PropertyImage> propertyImages) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.street = street;
        this.type = type;
        this.lotSize = lotSize;
        this.noOfBedrooms = noOfBedrooms;
        this.noOfBathrooms = noOfBathrooms;
        this.price = price;
        this.status = status;
        this.services = services;
        this.features = features;
        this.description = description;
        this.propertyImages = propertyImages;
    }



    public Property(Long id, State state, City city, String street,
                    Property.PropertyType type, int lotSize, int noOfBedrooms,
                    int noOfBathrooms,double price, Property.PropertyStatus status,
                    Set<Property.PropertyServices> services, Set<Property.PropertyFeatures> features,
                    String owners, String description, List<PropertyImage> propertyImages) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.street = street;
        this.type = type;
        this.lotSize = lotSize;
        this.noOfBedrooms = noOfBedrooms;
        this.noOfBathrooms = noOfBathrooms;
        this.price = price;
        this.status = status;
        this.services = services;
        this.features = features;
        this.owners = owners;
        this.description = description;
        this.propertyImages = propertyImages;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public void setLotSize(int lotSize) {
        this.lotSize = lotSize;
    }

    public void setNoOfBedrooms(int noOfBedrooms) {
        this.noOfBedrooms = noOfBedrooms;
    }

    public void setNoOfBathrooms(int noOfBathrooms) {
        this.noOfBathrooms = noOfBathrooms;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public void setServices(Set<Property.PropertyServices> services) {
        this.services = services;
    }

    public void setFeatures(Set<Property.PropertyFeatures> features) {
        this.features = features;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPropertyImages(List<PropertyImage> propertyImages) {
        this.propertyImages = propertyImages;
    }


}

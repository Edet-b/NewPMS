package com.example.newdemo.Entity;

import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotNull
    @ManyToOne
    @JoinColumn(name = "propertyId")
    private Property property;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] propertyImages;

    public PropertyImage() {
    }

    public PropertyImage(Long id, Property property, byte[] propertyImages) {
        this.id = id;
        this.property = property;
        this.propertyImages = propertyImages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPropertyImages(byte[] propertyImages) {
        this.propertyImages = propertyImages;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}

package com.example.newdemo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Getter
public class City {

    @Id
    @GeneratedValue 
    private Long id;

    @ManyToOne
    private State state;

    private String name;

    private String cityId;

    public City() {}

    public City(Long id, State state, String name, String cityId) {
        this.id = id;
        this.state = state;
        this.name = name;
        this.cityId = cityId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}

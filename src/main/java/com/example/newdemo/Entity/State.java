package com.example.newdemo.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;


@Entity
@Data
@Getter
public class State {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String stateId;

    public State() {
    }

    public State(Long id, String name, String stateId) {
        this.id = id;
        this.name = name;
        this.stateId = stateId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
}

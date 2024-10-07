package com.example.newdemo.Entity;


import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
public class UserImages {

    @Id
    @GeneratedValue
    private long id;

//    @NotNull
    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] userImages;

    public UserImages() {
    }

    public UserImages(long id, Users user, byte[] userImages) {
        this.id = id;
        this.user = user;
        this.userImages = userImages;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setUserImages(byte[] userImages) {
        this.userImages = userImages;
    }
}

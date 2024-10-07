package com.example.newdemo.Entity;

import jakarta.persistence.*;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class Users {

    public enum userRoles{
       Admin("Admin"), Manager("Manager"), Agent("Agent"), Client("Client");

        private final String name;
        userRoles(String name){
            this.name = name;
        }

    }

    @Id
    @GeneratedValue
    private Long id;

//    @NotEmpty(message = "Firstname is Required")
    private String firstName;

//    @NotEmpty(message = "Lastname is Required")
    private String lastName;

//    @NotEmpty(message = "Email is Required")
    @Column(unique = true, nullable = false)
    private String email;

//    @NotEmpty(message = "Phone number is Required")
    private String phoneNumber;

//    @NotEmpty(message = "State is Required")
    private String userState;

//    @NotEmpty(message = "City is Required")
    private String userCity;

//    @NotEmpty(message = "Street is Required")
    private String street;

//    @NotNull
    private int postalCode;

//    @NotNull
    private int houseNumber;

//    @NotEmpty(message = "Username is Required")
    @Column(unique = true, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
//    @NotNull
    private Users.userRoles userRoles;

    @ToString.Exclude
    @ManyToOne
    private State state;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<City> city;

//    @NotEmpty(message = "Password is Required")
    public String password;

    @OneToMany(mappedBy = "owners", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> properties;

    private LocalDateTime updatedAt;

//    @NotNull
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserImages> userImages = new ArrayList<>();
    
    public Users() {
    }

    public Users(Long id, String firstName, String lastName, String email,
                 String phoneNumber, String userState, String userCity, String street,
                 int postalCode, int houseNumber, String username, Users.userRoles userRoles,
                 String password, LocalDateTime updatedAt, List<UserImages> userImages) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userState = userState;
        this.userCity = userCity;
        this.street = street;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
        this.username = username;
        this.userRoles = userRoles;
        this.password = password;
        this.updatedAt = updatedAt;
        this.userImages = userImages;
    }

    public Users(Long id, String firstName, String lastName, String email,
                 String phoneNumber, String userState, String userCity,
                 String street, int postalCode, int houseNumber, String username,
                 Users.userRoles userRoles, State state, Set<City> city,
                 String password, LocalDateTime updatedAt, List<UserImages> userImages) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userState = userState;
        this.userCity = userCity;
        this.street = street;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
        this.username = username;
        this.userRoles = userRoles;
        this.state = state;
        this.city = city;
        this.password = password;
        this.updatedAt = updatedAt;
        this.userImages = userImages;
    }

    public Users(Long id, String firstName, String lastName, String email, String phoneNumber,
                 String userState, String userCity, String street, int postalCode, int houseNumber,
                 String username, Users.userRoles userRoles, String password, List<Property> properties,
                 LocalDateTime updatedAt, List<UserImages> userImages) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userState = userState;
        this.userCity = userCity;
        this.street = street;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
        this.username = username;
        this.userRoles = userRoles;
        this.password = password;
        this.properties = properties;
        this.updatedAt = updatedAt;
        this.userImages = userImages;
    }

    public Users(Long id, String firstName, String lastName, String email, String phoneNumber,
                 String userState, String userCity, String street, int postalCode, int houseNumber,
                 String username, Users.userRoles userRoles, State state, Set<City> city, String password,
                 List<Property> properties, LocalDateTime updatedAt, List<UserImages> userImages) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userState = userState;
        this.userCity = userCity;
        this.street = street;
        this.postalCode = postalCode;
        this.houseNumber = houseNumber;
        this.username = username;
        this.userRoles = userRoles;
        this.state = state;
        this.city = city;
        this.password = password;
        this.properties = properties;
        this.updatedAt = updatedAt;
        this.userImages = userImages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email= email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserRoles(Users.userRoles userRoles) {
        this.userRoles = userRoles;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setCity(Set<City> city) {
        this.city = city;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void setPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        this.password = Base64.getEncoder().encodeToString(hash);
    }

    public String getName(String firstName, String lastName){
        return lastName + " " + firstName;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUserImages(List<UserImages> userImages) {
        this.userImages = userImages;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}

package com.example.newdemo.View.Login;

import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.UserRepository;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;


//@CssImport("/generated/login.css")
//@Route(" ")
public class LoginView extends VerticalLayout {

    @Autowired
    UserRepository userRepository;
    public LoginView() {

        VerticalLayout mainLayout = new VerticalLayout();

        VerticalLayout layout = new VerticalLayout();
        mainLayout.setSizeFull();
        layout.addClassName("login-layout");

        H6 login = new H6("Login");
        login.addClassName("login-header");

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Login", event -> authenticate(usernameField.getValue(), passwordField.getValue()));

        layout.add(login, usernameField, passwordField, loginButton);

        mainLayout.add(layout);
        mainLayout.getStyle().setBackground("images/PMS_background.jpg");
        add(mainLayout);
    }

    private void authenticate(String username, String password) {
        Optional<Users> storedUsername = userRepository.findByUsername(username);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hash);

            if(storedUsername.isPresent()){
                String usernameString = storedUsername.get().getUsername();
                Optional<String> storedHashedPassword = userRepository.getHashedPasswordByUsername(usernameString);

                System.out.println(hashedPassword);
                System.out.println(storedHashedPassword);

                if(hashedPassword.equals(storedHashedPassword.get())) {
                    getUI().ifPresent(ui -> ui.navigate("dashboard"));
                } else {
                    Notification.show("Invalid password");
                }
            } else{
                Notification.show("Invalid username");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Notification.show("Authentication failed");
        }
    }
}


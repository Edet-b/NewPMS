package com.example.newdemo.View.UserViews;


import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Entity.Users;
import com.example.newdemo.Forms.UserForm;
import com.example.newdemo.Repository.UserRepository;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.Service.UserService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;


@CssImport("/generated/userForm.css")
@Route(value = "UserFormView", layout = MainView.class)
public class UserFormView extends VerticalLayout {
    StateService stateService;
    CityService cityService;

    UserService userService;

    UserForm userForm;

    UserView userView;
    Button backToUser = new Button("All Users");

    @Autowired
    UserRepository userRepository;

    @Autowired
    public UserFormView(StateService stateService, CityService cityService,
                        UserService userService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.userService = userService;

        userView = new UserView(stateService,cityService, userService);
        userView.addUser.setVisible(false);

        userForm = new UserForm(stateService.getAllStates(), cityService.getAllSetOfCities());
        userForm.setUser(new Users());
        userForm.delete.setVisible(false);
        userForm.city.setVisible(false);

        userForm.addSaveListener(this::save);
        userForm.addCloseListener(e -> close());

        backToUser.setPrefixComponent(new Icon(VaadinIcon.ARROW_LONG_LEFT));
        backToUser.addClickListener(e -> close());
        backToUser.getStyle().set("border", "0px").set("background-color", "white ");

        backToUser.addClassName("back-to-users");
        userForm.addClassName("users-form");

        add(backToUser, userForm);
        userForm.userRoles.addValueChangeListener(event -> {
            String userRole = event.getValue().toString();
            if("Client".equals(userRole) || "Admin".equals(userRole)){
                userForm.locationAccess.setVisible(false);
                userForm.state.setVisible(false);
                userForm.city.setVisible(false);
            } else{
                userForm.locationAccess.setVisible(true);
                userForm.state.setVisible(true);
            }
        });

        userForm.state.addValueChangeListener(event ->{
            State selectedState = event.getValue();
            getCityListForState(selectedState);
            userForm.city.setVisible(true);
        });
    }

    private void updateList(){
        userView.userGrid.setItems(userService.getAllUsers());
    }

    private void save(UserForm.SaveEvent event){

        String email = event.getUser().getEmail();
        String username = event.getUser().getUsername();

        Optional<Users> userEmailRepo = userRepository.findByEmail(email);
        Optional<Users> userNameRepo = userRepository.findByUsername(username);

        if(userEmailRepo.isPresent()){
            Notification notification = new Notification("Email already Exists", 3000, Notification.Position.MIDDLE);
            notification.open();

        }
        if(userNameRepo.isPresent()) {
            Notification notification = new Notification("Username already exists", 3000, Notification.Position.MIDDLE);
            notification.open();
        }
            event.getUser().setUpdatedAt(LocalDateTime.now());
            userService.saveUsers(event.getUser());
            updateList();
    }

    public void close(){
        updateList();
        UI.getCurrent().navigate(UserView.class);
    }

    public void getCityListForState(State state){
       Set<City> cityByState =  cityService.getAllCitiesByState(state);
        userForm.city.setItems(cityByState);
        userForm.city.setItemLabelGenerator(City::getName);
    }
}

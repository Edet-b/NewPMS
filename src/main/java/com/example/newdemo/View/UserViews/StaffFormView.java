package com.example.newdemo.View.UserViews;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Entity.UserImages;
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
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static com.example.newdemo.View.PropertyViews.PropertyFormView.resizeImage;

@CssImport("/generated/userForm.css")
@Route(value = "staffFormView", layout = MainView.class)
public class StaffFormView extends VerticalLayout {
    StateService stateService;
    CityService cityService;
    UserService userService;
    UserForm userForm;
    StaffView userView;
    Button backToUser = new Button("All Users");
    byte[] resizedImageData;
    List<byte[]> resizedList = new ArrayList<>();
    @Autowired
    UserRepository userRepository;

    @Autowired
    public StaffFormView(StateService stateService, CityService cityService,
                         UserService userService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.userService = userService;

        userView = new StaffView(stateService,cityService, userService);
        userView.addUser.setVisible(false);

        userForm = new UserForm(stateService.getAllStates(), cityService.getAllSetOfCities());
        userForm.setUser(new Users());
        userForm.delete.setVisible(false);
        userForm.city.setVisible(false);

        userForm.addSaveListener(this::save);
        userForm.addCloseListener(e -> close());

        backToUser.setPrefixComponent(new Icon(VaadinIcon.ARROW_LONG_LEFT));
        backToUser.addClickListener(e -> close());
        backToUser.getStyle().set("border", "0px").set("background-color", "#F7F5F5");

        backToUser.addClassName("back-to-users");
        userForm.addClassName("users-form");

        userForm.userRoles.setItems(Users.userRoles.Admin, Users.userRoles.Manager, Users.userRoles.Agent);

        userForm.userImages.addSucceededListener(event -> {
            String imageName = event.getFileName();
            InputStream inputStream = userForm.buffer.getInputStream(imageName);
            try{
                byte[] originalImageData = inputStream.readAllBytes();

                int targetWidth = 50;
                int targetHeight = 50;

                resizedImageData = resizeImage(originalImageData, targetWidth, targetHeight);
                resizedList.add(resizedImageData);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        userForm.userRoles.addValueChangeListener(event -> {
            String userRole = event.getValue().toString();
            if("Admin".equals(userRole)){
                userForm.locationAccess.setVisible(false);
                userForm.state.setVisible(false);
                userForm.city.setVisible(false);
            } else{
                userForm.locationAccess.setVisible(true);
                userForm.state.setVisible(true);
            }
        });

        userForm.state.addValueChangeListener(event -> {
            State selectedState = event.getValue();
            getCityListForState(selectedState);
            userForm.city.setVisible(true);
        });

        add(backToUser, userForm);
    }

   private void updateList(){
        userView.userGrid.setItems(userService.getAllUsersButClients());
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

        for (byte[] resizedImageData : resizedList) {
            Users user = event.getUser();
            UserImages userImages = new UserImages();
            userImages.setUser(user);
            userImages.setUserImages(resizedImageData);
            user.getUserImages().add(userImages);
        }
            userService.saveUsers(event.getUser());
            updateList();
    }

    public void close(){
        UI.getCurrent().navigate(StaffView.class);
        updateList();
    }

    public void getCityListForState(State state){
       Set<City> cityByState =  cityService.getAllCitiesByState(state);
        userForm.city.setItems(cityByState);
        userForm.city.setItemLabelGenerator(City::getName);
    }
}

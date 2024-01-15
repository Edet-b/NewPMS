package com.example.newdemo.View.UserViews;


import com.example.newdemo.Entity.Users;
import com.example.newdemo.Forms.UserForm;
import com.example.newdemo.Repository.UserRepository;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.Service.UserService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CssImport("/generated/users.css")
@Route(value = "userView", layout = MainView.class)
public class UserView extends VerticalLayout implements RouterLayout {

    Grid<Users> userGrid = new Grid<>(Users.class, false);

    TextField filterText = new TextField();
    StateService stateService;

    CityService cityService;
    UserService userService;

    @Autowired
    UserRepository userRepository;

    ComboBox<Users.userRoles> newUserRoles = new ComboBox<Users.userRoles>("User Roles");

    UserForm userForm;
    public Button addUser;
    Dialog editDialog = new Dialog();
    private boolean isUserGridListenerActive = true;

    @Autowired
    public UserView(StateService stateService, CityService cityService, UserService userService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.userService = userService;

        userForm = new UserForm(stateService.getAllStates(), cityService.getAllSetOfCities());
        newUserRoles.setItems(Users.userRoles.values());

        setSizeFull();
        configureGrid();
        getToolbar();

        updateList();
        add(getToolbar(), userGrid);
        updateList();
    }

    private void configureGrid() {
        userGrid.addColumn(users -> users.getName(users.getLastName(), users.getFirstName())).setHeader("Name").setSortable(true);
        userGrid.addColumn(Users::getUsername).setHeader("Username").setSortable(true);
        userGrid.addColumn(Users::getUserRoles).setHeader("User Role").setSortable(true);
        userGrid.addColumn(Users::getEmail).setHeader("Email").setSortable(true);
        userGrid.addColumn(Users::getPhoneNumber).setHeader("Phone Number").setSortable(true);
        userGrid.addColumn(Users::getUserState).setHeader("Location").setSortable(true);

        List<Users> usersList = userService.getAllUsers();
        userGrid.setItems(usersList);
        userGrid.addClassName("grid");

        userGrid.addItemClickListener(event -> editUser(event.getItem()));
        updateList();
    }

    private HorizontalLayout getToolbar() {

        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        filterText.setPlaceholder("  Search user");
        filterText.setClearButtonVisible(true);
        filterText.setPrefixComponent(searchIcon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.addClassName("search-users");
        filterText.getStyle().set("margin-top", "32px");

        Icon addIcon = new Icon(VaadinIcon.PLUS);
        addUser = new Button("New User");
        addUser.setPrefixComponent(addIcon);
        addUser.addClassName("add-users");

        addUser.addClickListener(clickEvent ->
            UI.getCurrent().navigate(UserFormView.class));

        newUserRoles.setClearButtonVisible(true);
        newUserRoles.addValueChangeListener(e -> {
            if(e.getValue() != null){
                 userGrid.setItems(userService.getAllUsersByUserRole(e.getValue()));
            } else {
                updateList();
        }});

        newUserRoles.addClassName("new-user-roles");


        var toolbar = new HorizontalLayout(addUser, filterText, newUserRoles);
        toolbar.addClassName("UserToolBar");
        return toolbar;
    }

    private void updateList(){
        if(filterText != null) {
            userGrid.setItems(userService.getAllUsersByFilter(filterText.getValue()));
        }else{
            userGrid.setItems(userService.getAllUsers());
        }
    }

    private void editUser(Users user){
        if(user != null) {

            isUserGridListenerActive = false;
            String userRole = user.getUserRoles().toString();

            userForm.setUser(user);

            userForm.city.setVisible(false);
            userForm.password.setVisible(false);

            userForm.addSaveListener(this::saveEdit);
            userForm.addDeleteListener(this::deleteEdit);
            userForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("User");
            editDialog.getFooter().add(userForm.buttonLayout());

            if (userRole.equals("Admin") || userRole.equals("Client")) {
                userForm.locationAccess.setVisible(false);
                userForm.state.setVisible(false);
                editDialog.add(userForm);
            }
             else {
                userForm.locationAccess.setVisible(true);
                userForm.state.setVisible(true);
//                userForm.city.setVisible(true);
                editDialog.add(userForm);
             }
            editDialog.open();
            isUserGridListenerActive = true;
        }
    }

    private void saveEdit(UserForm.SaveEvent event) {

        String email = event.getUser().getEmail();
        String username = event.getUser().getUsername();
        String name = event.getUser().getName(event.getUser().getFirstName(), event.getUser().getLastName());

        Optional<Users> userEmailRepo = userRepository.findByEmail(email);
        Optional<Users> userNameRepo = userRepository.findByUsername(username);

        boolean emails = userEmailRepo.isPresent();
        boolean usernames = userNameRepo.isPresent();

        boolean emailExists = false;
        boolean usernameExists = false;

        if (emails) {
            Users userWithEmail = userEmailRepo.get();
            String firstNameFromEmail = userWithEmail.getFirstName();
            String lastNameFromEmail = userWithEmail.getLastName();

            String firstAndLastNameFromEmail = firstNameFromEmail + " " + lastNameFromEmail;
            if (!firstAndLastNameFromEmail.equals(name)) {
                emailExists = true;
            }
        }

        if (usernames) {
            Users userWithUsername = userNameRepo.get();
            String firstNameFromUsername = userWithUsername.getFirstName();
            String lastNameFromUsername = userWithUsername.getLastName();

            String firstAndLastNameFromUsername = firstNameFromUsername + " " + lastNameFromUsername;
            if (!firstAndLastNameFromUsername.equals(name)) {
                usernameExists = true;
            }
        }

        if (emailExists) {
            Notification notification = new Notification("Email already Exists", 1000, Notification.Position.MIDDLE);
            notification.open();
        }

        if (usernameExists) {
            Notification notification = new Notification("Username already exists", 1000, Notification.Position.MIDDLE);
            notification.open();
        }

        if (!emailExists && !usernameExists) {
            event.getUser().setUpdatedAt(LocalDateTime.now());
            userService.saveUsers(event.getUser());
            updateList();
            closeEdit();
        }
    }


    private void deleteEdit(UserForm.DeleteEvent event) {
        userService.deleteUser(event.getUser());
        updateList();
        closeEdit();
    }

    private void closeEdit(){
        editDialog.close();
    }
}

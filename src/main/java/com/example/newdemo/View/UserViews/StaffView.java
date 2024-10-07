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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CssImport("/generated/users.css")
@Route(value = "staffView", layout = MainView.class)
public class StaffView extends VerticalLayout implements RouterLayout {

    Grid<Users> userGrid = new Grid<>(Users.class, false);
    TextField filterText = new TextField();
    StateService stateService;
    CityService cityService;
    UserService userService;
    Users originalUser;
    @Autowired
    UserRepository userRepository;
    ComboBox<Users.userRoles> newUserRoles = new ComboBox<>("User Roles");

    UserForm userForm;
    public Button addUser;
    Dialog editDialog = new Dialog();
    private boolean isUserGridListenerActive = true;

    @Autowired
    public StaffView(StateService stateService, CityService cityService, UserService userService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.userService = userService;

        userForm = new UserForm(stateService.getAllStates(), cityService.getAllSetOfCities());
        newUserRoles.setItems(Users.userRoles.Admin, Users.userRoles.Manager, Users.userRoles.Agent);

        setSizeFull();
        configureGrid();
        getToolbar();

        Tab client = new Tab(new RouterLink("Client", ClientView.class));
        Tab staff = new Tab(new RouterLink("Staff", StaffView.class));

        client.addClassName("location-items");
        staff.addClassName("location-items");

        Tabs userTabs = new Tabs(client, staff);
        userTabs.addClassName("location-tabs");

        userTabs.setSelectedTab(staff);
        HorizontalLayout display = new HorizontalLayout(userTabs);
        display.addClassName("location-navbar");

        updateList();
        add(display, getToolbar(), userGrid);
        updateList();
    }

    private void configureGrid() {
        userGrid.addColumn(users -> users.getName(users.getLastName(), users.getFirstName())).setHeader("Name").setSortable(true);
        userGrid.addColumn(Users::getUsername).setHeader("Username").setSortable(true);
        userGrid.addColumn(Users::getUserRoles).setHeader("User Role").setSortable(true);
        userGrid.addColumn(Users::getEmail).setHeader("Email").setSortable(true);
        userGrid.addColumn(Users::getPhoneNumber).setHeader("Phone Number").setSortable(true);
        userGrid.addColumn(Users::getUserState).setHeader("Location").setSortable(true);

        List<Users> usersList = userService.getAllUsersButClients();
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
                UI.getCurrent().navigate(StaffFormView.class));

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
            userGrid.setItems(userService.getAllOtherUsersByFilter(filterText.getValue()));
        }else{
            userGrid.setItems(userService.getAllUsersButClients());
        }
    }

    private void editUser(Users user){
        if(user != null) {

            isUserGridListenerActive = false;
            String userRole = user.getUserRoles().toString();

            userForm.setUser(user);
            userForm.resetBackToOriginalData(user);

            if(originalUser == null){
                originalUser = new Users();
            }

            originalUser.setEmail(user.getEmail());
            originalUser.setUsername(user.getUsername());

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
                editDialog.add(userForm);
            }
            editDialog.open();
            isUserGridListenerActive = true;
        }
    }

    private void saveEdit(UserForm.SaveEvent event) {

        String email = event.getUser().getEmail();
        String username = event.getUser().getUsername();

        boolean emailChanged = !email.equals(originalUser.getEmail());
        boolean usernameChanged = !username.equals(originalUser.getUsername());

        Optional<Users> userEmailRepo = userRepository.findByEmail(email);
        Optional<Users> userNameRepo = userRepository.findByUsername(username);

        if (emailChanged && userEmailRepo.isPresent()) {
            Notification.show("Email already exist", 1500, Notification.Position.BOTTOM_START);
        } else if(usernameChanged && userNameRepo.isPresent()){
            Notification.show("Username already exist", 1500, Notification.Position.BOTTOM_START);
        } else {
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
        userForm.resetForm();
        editDialog.close();
    }


}


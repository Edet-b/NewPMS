package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Entity.Users;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;
import java.util.Set;


@CssImport("/generated/userForm.css")
public class UserForm extends FormLayout {
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    public EmailField email = new EmailField("Email");
    TextField phoneNumber = new TextField("Phone Number");
    TextField userState = new TextField("State");
    TextField userCity = new TextField("City");
    TextField street = new TextField("Street");
    IntegerField postalCode = new IntegerField("Postal Code");
    IntegerField houseNumber = new IntegerField("HouseNumber");
    public TextField username = new TextField("User Name");
    public ComboBox<Users.userRoles> userRoles = new ComboBox<>("User Roles");
    public  ComboBox<State> state = new ComboBox<>("State");
    public CheckboxGroup<City> city = new CheckboxGroup<>("City");
    public MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    public Upload userImages = new Upload(buffer);
    public H3 locationAccess;
    Users originalData;
   Binder<Users> userBinder = new Binder<>(Users.class);

    Button save = new Button("Save");
    Button discardChanges = new Button("Discard Changes");
    public Button delete = new Button("Delete");
    public PasswordField password = new PasswordField("Password");

    public UserForm(List<State> states, Set<City> cities){
        setSizeFull();
        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        city.setItems(cities);
        city.setItemLabelGenerator(City::getName);

        userImages.setAcceptedFileTypes("image/jpeg", "image/png");
        userRoles.setItems(Users.userRoles.values());

        userBinder.bindInstanceFields(this);

        H3 profileInfo = new H3("Profile Information");
        H3 address = new H3("Address");
        H3 userData = new H3("UserData");
        locationAccess = new H3("Location Access");

        profileInfo.addClassName("sub-titles");
        address.addClassName("sub-titles");
        userData.addClassName("sub-titles");
        locationAccess.addClassName("sub-titles");
        password.addClassName("user-password");

        profileInfo.getStyle().set("margin-top", "0px");

        password.setMinLength(8);

        FormLayout fLE = new FormLayout(firstName, lastName, email);
        FormLayout pNu = new FormLayout(phoneNumber);
        FormLayout pH = new FormLayout(postalCode, houseNumber);
        FormLayout uUS = new FormLayout(userState, userCity, street);
        FormLayout userDR = new FormLayout(username, userRoles);
        FormLayout stateGeneral = new FormLayout(state);
        FormLayout cityGeneral = new FormLayout(city);
        FormLayout pass = new FormLayout(password);

        fLE.setResponsiveSteps(new ResponsiveStep("0", 3));
        pNu.setResponsiveSteps(new ResponsiveStep("0", 3));
        pH.setResponsiveSteps(new ResponsiveStep("0", 3));
        uUS.setResponsiveSteps(new ResponsiveStep("0", 3));
        userDR.setResponsiveSteps(new ResponsiveStep("0", 3));
        stateGeneral.setResponsiveSteps(new ResponsiveStep("0", 3));
        cityGeneral.setResponsiveSteps(new ResponsiveStep("0", 3));
        pass.setResponsiveSteps(new ResponsiveStep("0", 3));


        FormLayout userFormLayout = new FormLayout(
                profileInfo, fLE, pNu, address, uUS, pH, userData, userDR,
                locationAccess, stateGeneral, cityGeneral, pass, userImages,
                buttonLayout()
        );

        userFormLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
        userFormLayout.setSizeFull();

        userFormLayout.getElement().getStyle().set("width", "fit-content");

        userFormLayout.addClassName("user-form-layout");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(userFormLayout);
        mainLayout.addClassName("users-main-layout");
        add(mainLayout);
    }
    public void resetBackToOriginalData(Users user){
        originalData = new Users();
        originalData.setFirstName(user.getFirstName());
        originalData.setLastName(user.getLastName());
        originalData.setUsername(user.getUsername());
        originalData.setPhoneNumber(user.getPhoneNumber());
        originalData.setUserState(user.getUserState());
        originalData.setUserCity(user.getUserCity());
        originalData.setHouseNumber(user.getHouseNumber());
        originalData.setPostalCode(user.getPostalCode());
        originalData.setStreet(user.getStreet());
        originalData.setUsername(user.getUsername());
        originalData.setUserRoles(user.getUserRoles());
        originalData.setState(user.getState());
        originalData.setCity(user.getCity());
        originalData.setUserImages(user.getUserImages());
    }

    public void resetForm(){
        userBinder.readBean(originalData);
    }

    public HorizontalLayout buttonLayout() {
        save.addClickShortcut(Key.ENTER);
        discardChanges.addClickShortcut(Key.ESCAPE);

        save.addClassName("users-save");
        delete.addClassName("users-delete");
        discardChanges.addClassName("users-cancel");

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> fireEvent(new DeleteEvent(this, userBinder.getBean())));
        discardChanges.addClickListener(clickEvent -> fireEvent(new CloseEvent(this)));

        userBinder.addStatusChangeListener(event -> save.setEnabled(userBinder.isValid()));

        return new HorizontalLayout(discardChanges, delete, save);
    }

    public void setUser(Users user){
        userBinder.setBean(user);
    }

    private void validateAndSave(){
        if(userBinder.isValid()){
            fireEvent(new SaveEvent(this, userBinder.getBean()));
        }
    }

    @Getter
    public static abstract  class UserFormEvent extends ComponentEvent<UserForm>{
        private final Users user;

        protected UserFormEvent(UserForm source, Users user){
            super(source, false);
            this.user = user;
        }
    }

    public static class SaveEvent extends UserFormEvent{
        SaveEvent(UserForm source, Users user){
            super(source, user);
        }
    }

    public static class DeleteEvent extends  UserFormEvent{
        DeleteEvent(UserForm source, Users user){
            super(source, user);
        }
    }

    public static class CloseEvent extends  UserFormEvent{
        CloseEvent(UserForm source){
            super(source, null);
        }
    }

    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener){
        return addListener(DeleteEvent.class, listener);
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener){
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener){
        return addListener(CloseEvent.class, listener);
    }

}

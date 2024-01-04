package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Entity.Users;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.data.binder.BeanValidationBinder;
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
    public ComboBox<Users.userRoles> userRoles = new ComboBox<Users.userRoles>("User Roles");

    public  ComboBox<State> state = new ComboBox<State>("State");

    public CheckboxGroup<City> city = new CheckboxGroup<>("City");
    public H3 locationAccess;

    Binder<Users> userBinder = new BeanValidationBinder<>(Users.class);

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

        userRoles.setItems(Users.userRoles.values());

        userBinder.bindInstanceFields(this);

        H3 profileInfo = new H3("Profile Information");
        H3 address = new H3("Address");
        H3 userData = new H3("UserData");
        locationAccess = new H3("Location Access");

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
                profileInfo,
                fLE,
                pNu,
                address,
                uUS,
                pH,
                userData,
                userDR,
                locationAccess,
                stateGeneral,
                cityGeneral,
                pass,
                buttonLayout()
        );

        userFormLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
        userFormLayout.setSizeFull();

        userFormLayout.getElement().getStyle().set("width", "fit-content");

        userFormLayout.addClassName("UserFormLayout");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(userFormLayout);
        mainLayout.addClassName("MainLayoutUsers");
        add(mainLayout);

    }



    public HorizontalLayout buttonLayout() {
        save.addClassName("UsersSave");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        discardChanges.addClassName("UsersDiscard");

        save.addClickShortcut(Key.ENTER);
        discardChanges.addClickShortcut(Key.ESCAPE);

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

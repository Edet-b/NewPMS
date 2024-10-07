package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.util.List;

@CssImport("/generated/locationView.css")
public class CityForm extends FormLayout {

    public ComboBox<State> state = new ComboBox<>("State");
    public TextField name = new TextField("Name");
    public TextField cityId = new TextField("City Id");

    Button save = new Button("Save");
    Button cancel = new Button("Discharge Changes");
    public Button delete = new Button("Delete");

    Binder<City> cityBinder = new Binder<>(City.class);

    public CityForm(List<State> states){
        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        cityBinder.bindInstanceFields(this);

        FormLayout cityFormLayout = new FormLayout(state, name, cityId);
        cityFormLayout.setResponsiveSteps( new ResponsiveStep("0", 3));
        add(cityFormLayout);
    }

    public HorizontalLayout buttonLayout() {
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClassName("save-button");
        delete.addClassName("delete-button");
        cancel.addClassName("cancel-button");

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> fireEvent(new DeleteEvent(this, cityBinder.getBean())));
        cancel.addClickListener(clickEvent -> fireEvent(new CloseEvent(this)));

        cityBinder.addStatusChangeListener(event -> save.setEnabled(cityBinder.isValid()));

        return new HorizontalLayout(cancel, delete, save);
    }

    private void validateAndSave(){
        if(cityBinder.isValid()){
            fireEvent(new SaveEvent(this, cityBinder.getBean()));
        }
    }

    public void setCity(City city){
        cityBinder.setBean(city);
    }

//    @Getter
    public static abstract class CityFormEvent extends ComponentEvent<CityForm>{
        private final City city;
        protected CityFormEvent(CityForm source, City city){
            super(source, false);
            this.city = city;
        }
        public City getCity(){
            return city;
        }
    }

    public static class SaveEvent extends CityFormEvent {
        SaveEvent(CityForm source, City city){
            super(source, city);
        }
    }

    public static class DeleteEvent extends CityFormEvent {
        DeleteEvent( CityForm source, City city){
            super(source, city);
        }
    }

    public static class CloseEvent extends CityFormEvent{
        CloseEvent(CityForm source){
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

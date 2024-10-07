package com.example.newdemo.Forms;

import com.example.newdemo.Entity.*;
import com.example.newdemo.Service.PropertyService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FinanceForm extends FormLayout{

    public ComboBox<Users> owner = new ComboBox<>("Owner");
    public ComboBox<State> state = new ComboBox<>("State");
    public ComboBox<City> city = new ComboBox<>("City");
    public ComboBox<Phrases> phrase = new ComboBox<>("Phrases");
    public ComboBox<Property.PropertyType> type = new ComboBox<>("Property Type");
    public NumberField amountPaid = new NumberField("Amount Paid");
    public TextField paidBy = new TextField("Paid By");
    Binder<Finances> financesBinder = new Binder<>(Finances.class);
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");

    @Autowired
    PropertyService propertyService;

    public FinanceForm(List<Users> users, List<State> states, List<City> cities, List<Phrases> phrases){

        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        city.setItems(cities);
        city.setItemLabelGenerator(City::getName);

        phrase.setItems(phrases);
        phrase.setItemLabelGenerator(Phrases::getName);

        owner.setItems(users);
        owner.setItemLabelGenerator(Users::toString);

        financesBinder.bindInstanceFields(this);

        FormLayout financeFormLayout = new FormLayout(owner, state, city, phrase, type, paidBy, amountPaid);
        financeFormLayout.setResponsiveSteps(new ResponsiveStep("0", 3));
        financeFormLayout.setSizeFull();
        financeFormLayout.addClassName("finance-form-layout");
        financeFormLayout.getStyle().set("width", "fit-content");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.addClassName("finance-main-layout");
        mainLayout.add(financeFormLayout);
        add(mainLayout);
    }

    public HorizontalLayout financeButtonLayout(){
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(clickEvent -> validateAndSave());
        cancel.addClickListener(clickEvent ->fireEvent(new CloseEvent(this)));

        return  new HorizontalLayout(cancel, save);
    }

    private void validateAndSave(){
        if(financesBinder.isValid()){
            fireEvent(new FinanceForm.SaveEvent(this, financesBinder.getBean()));
        }
    }

    public void setFinances(Finances finances){
        financesBinder.setBean(finances);
    }

    @Getter
    public static abstract class FinanceFormEvent extends ComponentEvent<FinanceForm>{
        private final Finances finances;

        protected FinanceFormEvent(FinanceForm source, Finances finances){
            super(source, false);
            this.finances = finances;
        }
    }
    public static class SaveEvent extends FinanceFormEvent{
        SaveEvent(FinanceForm source, Finances finances){
            super(source, finances);
        }
    }

    public static class CloseEvent extends FinanceFormEvent{
        CloseEvent(FinanceForm source){
            super(source, null);
        }
    }
    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener){
        return addListener(SaveEvent.class, listener);
    }

    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener){
        return addListener(CloseEvent.class, listener);
    }
}

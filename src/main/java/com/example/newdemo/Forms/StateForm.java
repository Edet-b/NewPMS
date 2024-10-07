package com.example.newdemo.Forms;

import com.example.newdemo.Entity.State;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

@CssImport("/generated/locationView.css")
public class StateForm extends FormLayout {

    public TextField name = new TextField("Name");
    public TextField stateId = new TextField("State Id");

    Button save = new Button("Save");
    public Button delete = new Button("Delete");
    Button cancel = new Button("Discharge Changes");

    Binder<State> stateBinder = new Binder<>(State.class);
    public StateForm(){
        stateBinder.bindInstanceFields(this);
        FormLayout stateForm = new FormLayout(
                name, stateId);
        stateForm.setResponsiveSteps( new ResponsiveStep("0", 2));
        add(stateForm);

    }

    public HorizontalLayout buttonLayout(){
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClassName("save-button");
        delete.addClassName("delete-button");
        cancel.addClassName("cancel-button");

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> fireEvent(new DeleteEvent(this, stateBinder.getBean())));
        cancel.addClickListener(clickEvent -> fireEvent(new CloseEvent(this)));

        stateBinder.addStatusChangeListener(event -> save.setEnabled(stateBinder.isValid()));

        return new HorizontalLayout(cancel, delete, save);
    }

    public void setState(State state){
        stateBinder.setBean(state);
    }

    private void validateAndSave(){
        if(stateBinder.isValid()){
            fireEvent(new SaveEvent(this, stateBinder.getBean()));
        }
    }

    @Getter
    public static abstract class StateFormEvent extends ComponentEvent<StateForm> {
        private final State state;

        protected StateFormEvent(StateForm source, State state){
            super(source, false);
            this.state = state;
        }

    }

    public static class SaveEvent extends StateFormEvent{
        SaveEvent(StateForm source, State state){
            super(source, state);
        }
    }

    public static class DeleteEvent extends StateFormEvent{
        DeleteEvent(StateForm source, State state){
            super(source, state);
        }
    }

    public static class CloseEvent extends StateFormEvent{
        CloseEvent(StateForm source){
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




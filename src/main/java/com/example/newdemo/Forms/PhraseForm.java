package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phrases;
import com.example.newdemo.Entity.State;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class PhraseForm extends FormLayout {
    public ComboBox<State> state = new ComboBox<>("State");
    public ComboBox<City> city = new ComboBox<>("City");
    public TextField name = new TextField("Name");
    public TextField phraseId = new TextField("Phrase Id");
    Button save = new Button("Save");
    Button cancel = new Button("Discharge Changes");
    public Button delete = new Button("Delete");

    Binder<Phrases> phrasesBinder = new Binder<>(Phrases.class);

    public PhraseForm(List<State> states, List<City> cities){
        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        city.setItems(cities);
        city.setItemLabelGenerator(City::getName);

        phrasesBinder.bindInstanceFields(this);

        FormLayout phraseFormLayout = new FormLayout(state, city, name, phraseId);
        phraseFormLayout.setResponsiveSteps(new ResponsiveStep("0", 2));
        phraseFormLayout.setSizeFull();

        phraseFormLayout.addClassName("phrase-form-layout");
        phraseFormLayout.getStyle().set("width", "fit-content");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(phraseFormLayout);
        mainLayout.addClassName("phrase-main-layout");
        add(mainLayout);
    }

    public HorizontalLayout buttonLayout() {
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> fireEvent(new PhraseForm.DeleteEvent(this, phrasesBinder.getBean())));
        cancel.addClickListener(clickEvent -> fireEvent(new PhraseForm.CloseEvent(this)));

        phrasesBinder.addStatusChangeListener(event -> save.setEnabled(phrasesBinder.isValid()));

        return new HorizontalLayout(cancel, delete, save);
    }

    private void validateAndSave(){
        if(phrasesBinder.isValid()){
            fireEvent(new PhraseForm.SaveEvent(this, phrasesBinder.getBean()));
        }
    }

    public void setPhrases(Phrases phrases){
        phrasesBinder.setBean(phrases);
    }

    public static abstract class PhraseFormEvent extends ComponentEvent<PhraseForm> {
        private final Phrases phrases;
        protected PhraseFormEvent(PhraseForm source, Phrases phrases){
            super(source, false);
            this.phrases = phrases;
        }
        public Phrases getPhrases(){
            return phrases;
        }
    }

    public static class SaveEvent extends PhraseFormEvent {
        SaveEvent(PhraseForm source, Phrases phrases){
            super(source, phrases);
        }
    }

    public static class DeleteEvent extends PhraseFormEvent{
        DeleteEvent(PhraseForm source, Phrases phrases){
            super(source, phrases);
        }
    }

    public static class CloseEvent extends PhraseFormEvent {
        CloseEvent(PhraseForm source){
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

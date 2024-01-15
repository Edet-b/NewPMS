package com.example.newdemo.Forms;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Property;
import com.example.newdemo.Entity.State;
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
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

@CssImport("/generated/propertyForm.css")
public class PropertyForm extends FormLayout {

    public ComboBox<State> state = new ComboBox<>("State");
    public ComboBox<City> city = new ComboBox<>("City");
    public TextField street = new TextField("Street");
    public ComboBox<Property.PropertyType> type = new ComboBox<>("Property Type");
    public IntegerField lotSize = new IntegerField("Lot Size");
    public IntegerField noOfBedrooms = new IntegerField("No of Bedrooms");
    public IntegerField noOfBathrooms = new IntegerField("No of Bathrooms");
    public NumberField price = new NumberField("Price");
    public ComboBox<String> owners = new ComboBox<>("Clients");
    public ComboBox<Property.PropertyStatus> status = new ComboBox<>("Status");
    public CheckboxGroup<Property.PropertyServices> services = new CheckboxGroup<>("Services");
    public CheckboxGroup<Property.PropertyFeatures> features = new CheckboxGroup<>("Additional Features");
    public TextArea description = new TextArea("Description");
    public MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    public Upload propertyImages = new Upload(buffer);


    Binder<Property> propertyBinder = new BeanValidationBinder<>(Property.class);

    Button save = new Button("Save");
    public Button delete = new Button("Delete");
    Button cancel = new Button("Discharge Changes");


    public PropertyForm(List<State> states, List<City> cities){

        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        city.setItems(cities);
        city.setItemLabelGenerator(City::getName);

        status.setItems(Property.PropertyStatus.values());
        type.setItems(Property.PropertyType.values());

        services.setItems(Property.PropertyServices.values());
        features.setItems(Property.PropertyFeatures.values());

        description.setHeightFull();

        propertyImages.setAcceptedFileTypes("image/jpeg", "image/png");

        List<String> items = Arrays.asList("Edet Blessing", "Agun Dayo", "Yahaya Yusuf",
                "Dabirichi Soribe");
        owners.setItems(items);


        propertyBinder.bindInstanceFields(this);

        FormLayout sCS = new FormLayout(state, city, street);
        FormLayout tLP = new FormLayout(type, lotSize, price);
        FormLayout nNS = new FormLayout(noOfBedrooms, noOfBathrooms, status);
        FormLayout clients = new FormLayout(owners);

        sCS.setResponsiveSteps(new ResponsiveStep("0", 3));
        tLP.setResponsiveSteps(new ResponsiveStep("0", 3));
        nNS.setResponsiveSteps(new ResponsiveStep("0", 3));
        clients.setResponsiveSteps(new ResponsiveStep("0", 3));


        sCS.setSizeFull();
        tLP.setSizeFull();
        nNS.setSizeFull();

        H6 location = new H6("LOCATION");
        H6 propertyDetails = new H6("PROPERTY DETAILS");
        location.getStyle().set("margin-top", "8px");


        location.addClassName("sub-titles-properties");
        propertyDetails.addClassName("sub-titles-properties");

        propertyDetails.getStyle().set("margin-top", "20px");
        FormLayout propertyFormLayout = new FormLayout(
                location,
                sCS,
                propertyDetails,
                tLP,
                nNS,
                clients, services, features,
                description, propertyImages,
                buttonLayout()
                );

        propertyFormLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
        propertyFormLayout.setSizeFull();

        propertyFormLayout.getElement().getStyle().set("width", "fit-content");

        propertyFormLayout.addClassName("property-form-layout");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(propertyFormLayout);
        mainLayout.addClassName("property-main-layout");
        add(mainLayout);
    }

    public HorizontalLayout buttonLayout() {
        save.addClassName("save");
        delete.addClassName("delete");
        cancel.addClassName("cancel");

        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);

        cancel.getStyle().set("margin-right", "auto");

        save.addClickListener(clickEvent -> validateAndSave());
        delete.addClickListener(clickEvent -> fireEvent(new DeleteEvent(this, propertyBinder.getBean())));
        cancel.addClickListener(clickEvent -> fireEvent(new CloseEvent(this)));

        propertyBinder.addStatusChangeListener(event -> save.setEnabled(propertyBinder.isValid()));

        return new HorizontalLayout(cancel, delete, save);
    }



    private void validateAndSave(){
        if(propertyBinder.isValid()){
            fireEvent(new SaveEvent(this, propertyBinder.getBean()));
        }
    }

    public void setProperty(Property property){
        propertyBinder.setBean(property);
    }

    @Getter
    public static abstract class PropertyFormEvent extends ComponentEvent<PropertyForm>{
        private final Property property;

        protected PropertyFormEvent(PropertyForm source, Property property){
            super(source, false);
            this.property = property;
        }
    }

    public static class SaveEvent extends PropertyFormEvent{
        SaveEvent(PropertyForm source, Property property){
            super(source, property);
        }
    }

    public static class DeleteEvent extends  PropertyFormEvent{
        DeleteEvent(PropertyForm source, Property property){
            super(source, property);
        }
    }

    public static class CloseEvent extends PropertyFormEvent{
        CloseEvent(PropertyForm source){
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

package com.example.newdemo.View.PropertyViews;

import com.example.newdemo.Entity.*;
import com.example.newdemo.Forms.PropertyForm;
import com.example.newdemo.Repository.PropertyRepository;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PropertyService;
import com.example.newdemo.Service.StateService;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;



import java.io.ByteArrayInputStream;
import java.util.List;

@CssImport("/generated/propertyView.css")
@Route(value = "propertyView", layout = MainView.class)
public class PropertyView extends VerticalLayout implements RouterLayout {
    Grid<Property>  propertyGrid = new Grid<>(Property.class, false);
    StateService stateService;
    CityService cityService;
    PropertyService propertyService;

    PropertyForm propertyForm;
    public Button addProperty;

    Dialog editDialog = new Dialog();

    ComboBox<Property.PropertyStatus> status = new ComboBox<>("Status");

    ComboBox<State> state = new ComboBox<>("State");

    ComboBox<City> city = new ComboBox<>("City");

    ComboBox<Property.PropertyType> typeOfProperty = new ComboBox<>("Type");
    Button resetFilterButton = new Button("ResetFilters");

    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    public PropertyView(StateService stateService, CityService cityService, PropertyService propertyService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.propertyService = propertyService;

        propertyForm = new PropertyForm(stateService.getAllStates(), cityService.getAllCities());

        status.setItems(Property.PropertyStatus.values());
        typeOfProperty.setItems(Property.PropertyType.values());

        List<State> states = stateService.getAllStates();
        state.setItems(states);
        state.setItemLabelGenerator(State::getName);

        List<City> cities = cityService.getAllCities();
        city.setItems(cities);
        city.setItemLabelGenerator(City::getName);

        setSizeFull();
        getToolbar();
        configureGrid();

        updateList();
        add(getToolbar(), propertyGrid);
        updateList();
    }

    private void updateList() {
        propertyService.getAllProperties();
    }

    public HorizontalLayout getToolbar() {

        Icon addIcon = new Icon(VaadinIcon.PLUS);

        addProperty = new Button("Add Property");
        addProperty.addClassName("add-property");
        addProperty.setPrefixComponent(addIcon);

        addProperty.addClickListener(clickEvent -> {
            UI.getCurrent().navigate(PropertyFormView.class);
        });

        state.addValueChangeListener(e -> {
              State selectedState = e.getValue();
              getCityListForState(selectedState);
        });

        city.setEnabled(false);
        typeOfProperty.setEnabled(false);
        status.setEnabled(false);


        state.addValueChangeListener(event -> {
            city.setEnabled(true);
            performSearch();
        });

        city.addValueChangeListener(event -> {
            typeOfProperty.setEnabled(true);
            performSearch();
        });

        typeOfProperty.addValueChangeListener(event -> {
            status.setEnabled(true);
            performSearch();
        });
        status.addValueChangeListener(event -> performSearch());

        resetFilterButton.addClickListener(clickEvent -> {

            state.clear();
            city.clear();
            typeOfProperty.clear();
            status.clear();

            status.setEnabled(false);
            typeOfProperty.setEnabled(false);
            city.setEnabled(false);
            updateList();
        });

        resetFilterButton.addClassName("reset-filters");


        var toolbar = new HorizontalLayout(addProperty, state, city, typeOfProperty, status, resetFilterButton);
        toolbar.addClassName("PropertyToolBar");
        return toolbar;
    }

    private void performSearch() {
        Property.PropertyStatus selectedStatus = status.getValue();
        Property.PropertyType selectedType = typeOfProperty.getValue();
        State selectedState = state.getValue();
        City selectedCity = city.getValue();

        List<Property> searchResults = propertyRepository.searchByStatusStateTypeAndCity(selectedStatus, selectedType, selectedState, selectedCity);
        propertyGrid.setItems(searchResults);
    }


    private void configureGrid() {
        propertyGrid.addColumn(new ComponentRenderer<>(property -> {
            List<PropertyImage> imageList = property.getPropertyImages();

            if (imageList != null && !imageList.isEmpty()) {
                byte[] imageData = imageList.get(0).getPropertyImages();

                if (imageData != null && imageData.length > 0) {
                    StreamResource resource = new StreamResource("image.png", () -> new ByteArrayInputStream(imageData));
                    Image image = new Image(resource, "Image");
                    image.getStyle().set("height", "20px");
                    return new PropertyComponent(property);
                }
            }

            return new PropertyComponent(property);
        })).setHeader("Image");

        propertyGrid.addColumn(property -> property.getState().getName()).setHeader("State").setSortable(true);
        propertyGrid.addColumn(property -> property.getCity().getName()).setHeader("City").setSortable(true);
        propertyGrid.addColumn(Property::getType).setHeader("Property Type").setSortable(true);
        propertyGrid.addColumn(Property::getLotSize).setHeader("Lot Size").setSortable(true);
        propertyGrid.addColumn(Property::getStatus).setHeader("Status").setSortable(true);
        propertyGrid.addColumn(Property::getPriceFormattedToString).setHeader("Price").setSortable(true);


        List<Property> propertyList = propertyService.getAllProperties();
        propertyGrid.setItems(propertyList);

        updateList();
        propertyGrid.addItemClickListener(event -> editForm(event.getItem()));
        updateList();

        propertyGrid.addClassName("grid");
    }

    private void editForm(Property property) {
        if (property != null) {
            propertyForm.setProperty(property);
            VerticalLayout imageLayout = new VerticalLayout();

            List<PropertyImage> imageList = property.getPropertyImages();
            if (imageList != null && !imageList.isEmpty()) {

                // Clear the existing content of imageLayout before adding images
                imageLayout.removeAll();

                for (PropertyImage propertyImage : imageList) {
                    byte[] imageData = propertyImage.getPropertyImages();
                    if (imageData != null && imageData.length > 0) {
                        StreamResource resource = new StreamResource("image.png", () -> new ByteArrayInputStream(imageData));
                        Image image = new Image(resource, "Image");
                        image.getStyle().set("height", "200px").set("width", "200px").set("margin-left", "17px");
                        imageLayout.add(image);
                    }
                }
                propertyForm.addComponentAtIndex(0, imageLayout);
            } else {
                propertyForm.addComponentAtIndex(0, new PropertyComponent(property));
            }
            propertyForm.addSaveListener(this::saveEdit);
            propertyForm.addDeleteListener(this::deleteEdit);
            propertyForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("Property");
            editDialog.getFooter().add(propertyForm.buttonLayout());
            editDialog.add(propertyForm);

            if ("Land".equals(property.getType().toString())) {
                propertyForm.noOfBathrooms.setVisible(false);
                propertyForm.noOfBedrooms.setVisible(false);
                propertyForm.services.setVisible(false);
                propertyForm.features.setVisible(false);
                editDialog.open();
            } else if (!"Land".equals(property.getType().toString())) {
                propertyForm.noOfBathrooms.setVisible(true);
                propertyForm.noOfBedrooms.setVisible(true);
                propertyForm.services.setVisible(true);
                propertyForm.features.setVisible(true);
                editDialog.open();
            } else {
                Notification.show("Property is null").setPosition(Notification.Position.TOP_CENTER);
            }
        }
    }

    private void saveEdit(PropertyForm.SaveEvent event){
        propertyService.saveProperty(event.getProperty());
        updateList();
        closeEdit();
    }

    private void deleteEdit(PropertyForm.DeleteEvent event){
        propertyService.deleteProperty(event.getProperty());
        updateList();
        closeEdit();
    }

    private void closeEdit(){
        editDialog.close();
    }

    private void getCityListForState(State state){
        List<City> cityByState = cityService.getAllCitiesByStateByList(state);
        city.setItems(cityByState);
        city.setItemLabelGenerator(City::getName);
    }
}



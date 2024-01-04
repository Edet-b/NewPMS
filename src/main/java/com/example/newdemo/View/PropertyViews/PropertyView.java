package com.example.newdemo.View.PropertyViews;

import com.example.newdemo.Entity.Property;
import com.example.newdemo.Entity.PropertyImage;
import com.example.newdemo.Forms.PropertyForm;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PropertyService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
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

@Route(value = "propertyView", layout = MainView.class)
public class PropertyView extends VerticalLayout implements RouterLayout {
    Grid<Property>  propertyGrid = new Grid<>(Property.class, false);
    StateService stateService;
    CityService cityService;
    PropertyService propertyService;

    PropertyForm propertyForm;
    public Button addProperty;

    Dialog editDialog = new Dialog();

    @Autowired
    public PropertyView(StateService stateService, CityService cityService, PropertyService propertyService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.propertyService = propertyService;

        propertyForm = new PropertyForm(stateService.getAllStates(), cityService.getAllCities());

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
        addProperty.setPrefixComponent(addIcon);

        addProperty.addClickListener(clickEvent -> {
            UI.getCurrent().navigate(PropertyFormView.class);
        });

        var toolbar = new HorizontalLayout(addProperty);
        toolbar.addClassName("PropertyToolBar");
        return toolbar;
    }


    private void configureGrid() {
        propertyGrid.addColumn(new ComponentRenderer<>(property -> {
            List<PropertyImage> imageList = property.getPropertyImages();

            if (imageList != null && !imageList.isEmpty()) {
                byte[] imageData = imageList.get(0).getPropertyImages();

                if (imageData != null && imageData.length > 0) {
                    StreamResource resource = new StreamResource("image.png", () -> new ByteArrayInputStream(imageData));
                    Image image = new Image(resource, "Image");
                    image.getStyle().set("height", "50px");
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
        propertyGrid.addColumn(Property::getPrice).setHeader("Price").setSortable(true);


        List<Property> propertyList = propertyService.getAllProperties();
        propertyGrid.setItems(propertyList);

        updateList();
        propertyGrid.asSingleSelect().addValueChangeListener(event -> editForm(event.getValue()));
        updateList();
    }

    private void editForm(Property property) {
        if (property != null) {
            propertyForm.setProperty(property);
            propertyForm.addSaveListener(this::saveEdit);
            propertyForm.addDeleteListener(this::deleteEdit);
            propertyForm.addCloseListener(e -> closeEdit());
            propertyForm.newProperty.setVisible(false);

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

}


package com.example.newdemo.View.PropertyViews;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Property;
import com.example.newdemo.Entity.PropertyImage;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Forms.PropertyForm;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.ImageService;
import com.example.newdemo.Service.PropertyService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Route(value = "PropertyFormView", layout = MainView.class)
public class PropertyFormView extends VerticalLayout {
    StateService stateService;
    CityService cityService;
    PropertyService propertyService;
    ImageService imageService;
    PropertyForm propertyForm;
    PropertyView propertyView;
    Button backToProperty = new Button("Back To Property");
    byte[] resizedImageData;
    @Autowired
    public PropertyFormView(StateService stateService,  CityService cityService,
                            PropertyService propertyService, ImageService imageService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.propertyService = propertyService;
        this.imageService = imageService;


        propertyView = new PropertyView(stateService, cityService, propertyService);
        propertyView.addProperty.setVisible(false);

        propertyForm = new PropertyForm(stateService.getAllStates(),
                cityService.getAllCities());
        propertyForm.setProperty(new Property());
        propertyForm.delete.setVisible(false);
        propertyForm.owners.setVisible(false);


        propertyForm.propertyImages.addSucceededListener(event -> {
            String imageName = event.getFileName();
            InputStream inputStream = propertyForm.buffer.getInputStream(imageName);
            try{
                byte[] originalImageData = inputStream.readAllBytes();

                int targetWidth = 100;
                int targetHeight = 100;
                resizedImageData = resizeImage(originalImageData, targetWidth, targetHeight);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        propertyForm.addSaveListener(this::save);
        propertyForm.addCloseListener(e -> close());
        propertyForm.setWidthFull();

        propertyForm.addClassName("property-form");

        backToProperty.setPrefixComponent(new Icon(VaadinIcon.ARROW_LONG_LEFT));
        backToProperty.addClickListener(event -> close());
        backToProperty.addClassName("back-to-property");
        backToProperty.getStyle().set("border", "0px").set("background-color", "white ");

        add(backToProperty, propertyForm);

        propertyForm.city.setEnabled(false);

        propertyForm.state.addValueChangeListener(e -> {
            State selectedState = e.getValue();
            getCityListForState(selectedState);
            propertyForm.city.setEnabled(true);
        });

        propertyForm.type.addValueChangeListener(event -> {
            String propertyType = event.getValue().toString();
            if("Land".equals(propertyType)) {
                propertyForm.noOfBedrooms.setVisible(false);
                propertyForm.noOfBathrooms.setVisible(false);
                propertyForm.services.setVisible(false);
                propertyForm.features.setVisible(false);
            } else{
                propertyForm.noOfBedrooms.setVisible(true);
                propertyForm.noOfBathrooms.setVisible(true);
                propertyForm.services.setVisible(true);
                propertyForm.features.setVisible(true);
            }
        });

        propertyForm.status.addValueChangeListener(e ->{
            String status = e.getValue().toString();
            if("Under Offer".equals(status)){
                propertyForm.owners.setVisible(true);
            } else{
                propertyForm.owners.setVisible(false);
            }
        });

        updateList();
    }

    private void updateList() {
        propertyView.propertyGrid.setItems(propertyService.getAllProperties());
    }

    public void save(PropertyForm.SaveEvent event){

        Property property = event.getProperty();
        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setProperty(property);
        propertyImage.setPropertyImages(resizedImageData);
        property.getPropertyImages().add(propertyImage);

        propertyService.saveProperty(property);
        imageService.saveImageToDatabase(property, resizedImageData);
        updateList();
        close();
    }

    public void close(){
        UI.getCurrent().navigate(PropertyView.class);
    }



    public static byte[] resizeImage(byte[] imageData, int targetWidth, int targetHeight) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(imageData);
        BufferedImage originalImage = ImageIO.read(inputStream);

        // Resize the image
        Image resizedImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        bufferedResizedImage.getGraphics().drawImage(resizedImage, 0, 0, null);

        // Convert the resized image back to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedResizedImage, "jpg", baos);
        return baos.toByteArray();
    }

    private void getCityListForState(State state){
        List<City> cityByState = cityService.getAllCitiesByStateByList(state);
        propertyForm.city.setItems(cityByState);
        propertyForm.city.setItemLabelGenerator(City::getName);
    }

    }

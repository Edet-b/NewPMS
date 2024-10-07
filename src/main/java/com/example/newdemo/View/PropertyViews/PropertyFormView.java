package com.example.newdemo.View.PropertyViews;

import com.example.newdemo.Entity.*;
import com.example.newdemo.Forms.PropertyForm;
import com.example.newdemo.Forms.createNewUserForm;
import com.example.newdemo.Service.*;
import com.example.newdemo.View.MainView;
import com.example.newdemo.View.UserViews.ClientFormView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
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
import java.util.Comparator;
import java.util.List;

@Route(value = "PropertyFormView", layout = MainView.class)
public class PropertyFormView extends VerticalLayout {
    StateService stateService;
    CityService cityService;
    PropertyService propertyService;
    UserService userService;
    PhraseService phraseService;
    ImageService imageService;
    PropertyForm propertyForm;
    PropertyView propertyView;
    byte[] resizedImageData;
    List<byte[]> resizedList = new ArrayList<>();
    createNewUserForm newUserDialog = new createNewUserForm();
    public Dialog createNewUser = new Dialog();
    Button backToProperty = new Button("Back To Property");
    @Autowired
    public PropertyFormView(StateService stateService,  CityService cityService, PropertyService propertyService,
                            ImageService imageService, UserService userService, PhraseService phraseService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.propertyService = propertyService;
        this.imageService = imageService;
        this.userService = userService;
        this.phraseService = phraseService;

        createNewUser.setHeaderTitle("New User");
        createNewUser.getFooter().add(newUserDialog.createNewUserButtonLayout());
        createNewUser.add(newUserDialog);
        newUserDialog.cancel.addClickListener(e -> createNewUser.close());
        newUserDialog.create.addClickListener(e -> createButton());


        propertyView = new PropertyView(stateService, cityService, propertyService, userService, imageService, phraseService);
        propertyView.addProperty.setVisible(false);

        propertyForm = new PropertyForm(stateService.getAllStates(), cityService.getAllCities(),
                userService.findUserNamesByClientUserRole(), phraseService.getAllPhrases());
        propertyForm.setProperty(new Property());
        propertyForm.delete.setVisible(false);
        propertyForm.owners.setVisible(false);


        propertyForm.propertyImages.addSucceededListener(event -> {
            String imageName = event.getFileName();
            InputStream inputStream = propertyForm.buffer.getInputStream(imageName);
            try{
                byte[] originalImageData = inputStream.readAllBytes();

                int targetWidth = 50;
                int targetHeight = 50;
                resizedImageData = resizeImage(originalImageData, targetWidth, targetHeight);
                resizedList.add(resizedImageData);
            }
            catch (IOException e) {
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

        propertyForm.state.setItems(sortedState());
        propertyForm.state.setItemLabelGenerator(State::getName);

        propertyForm.city.setItems(sortedCity());
        propertyForm.city.setItemLabelGenerator(City::getName);

        propertyForm.phrase.setItems(sortedPhrase());
        propertyForm.phrase.setItemLabelGenerator(Phrases::getName);

        List<Users> users = userService.findUserNamesByClientUserRole();
        List<Users> sortedNames = users.stream()
                .sorted(Comparator.comparing(Users::toString))
                .toList();

        propertyForm.owners.setItems(sortedNames);
        propertyForm.owners.setItemLabelGenerator(Users::toString);
        propertyForm.owners.setClearButtonVisible(true);
        propertyForm.owners.addCustomValueSetListener(e -> {
                    if (!e.getDetail().equals(sortedNames.toString())) {
                        createNewUser.open();
                    }
                });

        propertyForm.city.setEnabled(false);
        propertyForm.phrase.setEnabled(false);

        propertyForm.state.addValueChangeListener(e -> {
            State selectedState = e.getValue();
            getCityListForState(selectedState);
            propertyForm.city.setEnabled(true);
        });

        propertyForm.city.addValueChangeListener(e -> {
            City selectedCity = e.getValue();
            getPhraseByCity(selectedCity);
            propertyForm.phrase.setEnabled(true);
        });

        propertyForm.phrase.addValueChangeListener(e -> {
            Phrases selectedPhrase = e.getValue();
            System.out.println(selectedPhrase);
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
            propertyForm.owners.setVisible("UnderOffer".equals(status) || "Sold".equals(status));
        });
        updateList();
    }

    private void updateList() {
        propertyView.propertyGrid.setItems(propertyService.getAllProperties());
    }

    private void createButton(){
        newUserDialog.create.addClickListener(e -> {
            createNewUser.close();
            UI.getCurrent().navigate(ClientFormView.class);
        });
    }

    public void save(PropertyForm.SaveEvent event){
        Property property = event.getProperty();
        property.setPhrases(propertyForm.phrase.getValue());

        for (byte[] resizedImageData : resizedList){

            PropertyImage propertyImages = new PropertyImage();
            propertyImages.setProperty(property);
            propertyImages.setPropertyImages(resizedImageData);
            property.getPropertyImages().add(propertyImages);
        }
        propertyService.saveProperty(event.getProperty());
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

    private void getPhraseByCity(City city){
        List<Phrases> phraseByCity = phraseService.getAllPhrasesByCity(city);
        propertyForm.phrase.setItems(phraseByCity);
        propertyForm.phrase.setItemLabelGenerator(Phrases::getName);
    }

    private List<State> sortedState(){
        List<State> states = stateService.getAllStates();
        return states.stream()
                .sorted(Comparator.comparing(State::getName))
                .toList();
    }

    private List<City> sortedCity(){
        List<City> cities = cityService.getAllCities();
        return cities.stream()
                .sorted(Comparator.comparing(City::getName))
                .toList();
    }

    private List<Phrases> sortedPhrase(){
        List<Phrases> phrases = phraseService.getAllPhrases();
        return phrases.stream()
                .sorted(Comparator.comparing(Phrases::getName))
                .toList();
    }
}

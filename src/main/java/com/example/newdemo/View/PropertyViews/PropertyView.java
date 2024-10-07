package com.example.newdemo.View.PropertyViews;

import com.example.newdemo.Entity.*;
import com.example.newdemo.Forms.PropertyForm;
import com.example.newdemo.Forms.createNewUserForm;
import com.example.newdemo.Repository.PropertyRepository;
import com.example.newdemo.Service.*;
import com.example.newdemo.View.MainView;
import com.example.newdemo.View.UserViews.ClientFormView;
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
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.newdemo.View.PropertyViews.PropertyFormView.resizeImage;

@CssImport("/generated/propertyView.css")
@Route(value = "propertyView", layout = MainView.class)
public class PropertyView extends VerticalLayout implements RouterLayout {
    Grid<Property>  propertyGrid = new Grid<>(Property.class, false);
    StateService stateService;
    CityService cityService;
    PropertyService propertyService;
    ImageService imageService;
    UserService userService;
    PhraseService phraseService;
    List<byte[]> resizedList = new ArrayList<>();
    PropertyForm propertyForm;

    public Button addProperty;
    Dialog editDialog = new Dialog();
    ComboBox<Property.PropertyStatus> status = new ComboBox<>("Status");
    ComboBox<State> state = new ComboBox<>("State");
    ComboBox<City> city = new ComboBox<>("City");
    ComboBox<Phrases> phrase = new ComboBox<>("Phrase");
    ComboBox<Property.PropertyType> typeOfProperty = new ComboBox<>("Type");
    Button resetFilterButton = new Button("ResetFilters");
    byte[] resizedImageData;
    createNewUserForm newUserDialog = new createNewUserForm();
    public Dialog createNewUser = new Dialog();

    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    public PropertyView(StateService stateService, CityService cityService,
                        PropertyService propertyService, UserService userService, ImageService imageService,
                        PhraseService phraseService){
        this.stateService = stateService;
        this.cityService = cityService;
        this.propertyService = propertyService;
        this.userService = userService;
        this.imageService = imageService;
        this.phraseService = phraseService;

        createNewUser.setHeaderTitle("New User");
        createNewUser.getFooter().add(newUserDialog.createNewUserButtonLayout());
        createNewUser.add(newUserDialog);
        newUserDialog.cancel.addClickListener(e -> createNewUser.close());
        newUserDialog.create.addClickListener(e -> createButton());

        propertyForm = new PropertyForm(stateService.getAllStates(), cityService.getAllCities(),
                userService.findUserNamesByClientUserRole(), phraseService.getAllPhrases());

        status.setItems(Property.PropertyStatus.values());
        typeOfProperty.setItems(Property.PropertyType.values());

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

        List<State> states = stateService.getAllStates();
        List<State> sortedStates = states.stream()
                .sorted(Comparator.comparing(State::getName))
                .toList();

        state.setItems(sortedStates);
        state.setItemLabelGenerator(State::getName);

        List<City> cities = cityService.getAllCities();
        List<City> sortedCities = cities.stream()
                .sorted(Comparator.comparing(City::getName))
                .toList();


        city.setItems(sortedCities);
        city.setItemLabelGenerator(City::getName);

        List<Phrases> phrases = phraseService.getAllPhrases();
        List<Phrases> sortedPhrases = phrases.stream()
                    .sorted(Comparator.comparing(Phrases::getName))
                    .toList();

        phrase.setItems(sortedPhrases);
        phrase.setItemLabelGenerator(Phrases::getName);


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

        setSizeFull();
        getToolbar();
        configureGrid();

        add(getToolbar(), propertyGrid);
        updateList();
    }

    private void updateList() {
        propertyService.getAllProperties();
        closeEdit();
    }

    private HorizontalLayout getToolbar() {
        addProperty = new Button("Add Property");
        addProperty.addClassName("add-property");
        addProperty.setPrefixComponent(new Icon(VaadinIcon.PLUS));

        addProperty.addClickListener(clickEvent -> {
            UI.getCurrent().navigate(PropertyFormView.class);
        });

        state.addValueChangeListener(e -> {
              State selectedState = e.getValue();
              getCityListForState(selectedState);
        });

        city.addValueChangeListener(e -> {
            City selectedCity = e.getValue();
            getPhraseByCity(selectedCity);
        });

        city.setEnabled(false);
        phrase.setEnabled(false);
        typeOfProperty.setEnabled(false);
        status.setEnabled(false);


        state.addValueChangeListener(event -> {
            city.setEnabled(true);
            performSearch();
        });

        city.addValueChangeListener(event -> {
            phrase.setEnabled(true);
            performSearch();
        });

        phrase.addValueChangeListener(event -> {
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
            phrase.clear();
            typeOfProperty.clear();
            status.clear();

            status.setEnabled(false);
            typeOfProperty.setEnabled(false);
            phrase.setEnabled(false);
            city.setEnabled(false);
            updateList();
        });

        resetFilterButton.addClassName("reset-filters");

        var toolbar = new HorizontalLayout(addProperty, state, city, phrase, typeOfProperty, status, resetFilterButton);
        toolbar.addClassName("PropertyToolBar");
        return toolbar;
    }

    private void performSearch() {
        Property.PropertyStatus selectedStatus = status.getValue();
        Property.PropertyType selectedType = typeOfProperty.getValue();
        State selectedState = state.getValue();
        City selectedCity = city.getValue();
        Phrases selectedPhrase = phrase.getValue();

        List<Property> searchResults = propertyRepository.searchByStatusStateTypeCityAndPhrases(selectedStatus, selectedType,
                selectedState, selectedCity, selectedPhrase);
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
        propertyGrid.addColumn(property -> property.getPhrases().getName()).setHeader("Phrases").setSortable(true);
        propertyGrid.addColumn(Property::getType).setHeader("Property Type").setSortable(true);
        propertyGrid.addColumn(Property::getLotSize).setHeader("Lot Size").setSortable(true);
        propertyGrid.addColumn(Property::getStatus).setHeader("Status").setSortable(true);
        propertyGrid.addColumn(Property::getPriceFormattedToString).setHeader("Price").setSortable(true);
        List<Property> propertyList = propertyService.getAllProperties();
        propertyGrid.setItems(propertyList);

        propertyGrid.addItemClickListener(event -> editForm(event.getItem()));
        updateList();
        propertyGrid.addClassName("grid");
    }
    private boolean imagesAdded = false;
    private void editForm(Property property) {

        if (property != null) {
            propertyForm.setProperty(property);
            propertyForm.addSaveListener(this::saveEdit);
            propertyForm.addDeleteListener(this::deleteEdit);
            propertyForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("Property");
            editDialog.getFooter().add(propertyForm.buttonLayout());
            editDialog.add(propertyForm);

            if (!imagesAdded) {
                HorizontalLayout horizontalLayout = new HorizontalLayout();
                horizontalLayout.setWidthFull();

                List<PropertyImage> imageList = property.getPropertyImages();

                if (imageList != null && !imageList.isEmpty()) {
                    horizontalLayout.getStyle().set("overflow", "auto"); // Enable scrolling

                    for (PropertyImage propertyImage : imageList) {
                        byte[] imageData = propertyImage.getPropertyImages();
                        if (imageData != null && imageData.length > 0) {
                            StreamResource resource = new StreamResource("image.png", () -> new ByteArrayInputStream(imageData));
                            resource.setContentType("image/png"); // Ensure correct MIME type
                            Image image = new Image(resource, "Image");
                            image.getStyle().set("height", "500px").set("width", "auto"); // Fixed height, automatic width

                            // Log image data length, resource creation, and image creation
                            System.out.println("Image Data Length: " + imageData.length);
                            System.out.println("Resource Created: " + resource);
                            System.out.println("Image Created: " + image);

                            horizontalLayout.add(image);
                        }
                    }
                    Scroller scroller = new Scroller(horizontalLayout);
                    scroller.setWidth("100%"); // Full width
                    scroller.setHeight("600px"); // Set your desired fixed height

                    propertyForm.addComponentAtIndex(0, scroller);
                    imagesAdded = true;
                } else {
                    propertyForm.addComponentAtIndex(0, new PropertyComponent(property));
                }
            }
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
            propertyForm.status.addValueChangeListener(e ->{
                String status = e.getValue().toString();
                propertyForm.owners.setVisible(!status.equals("Available"));
            });
        }
    }

    public void saveEdit(PropertyForm.SaveEvent event){
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
        closeEdit();
    }


    private void deleteEdit(PropertyForm.DeleteEvent event){
        propertyService.deleteProperty(event.getProperty());
        updateList();
    }

    private void closeEdit(){
        editDialog.close();
        UI.getCurrent().navigate("propertyView");
    }

    private void getCityListForState(State state){
        List<City> cityByState = cityService.getAllCitiesByStateByList(state);
        city.setItems(cityByState);
        city.setItemLabelGenerator(City::getName);
    }

    private void getPhraseByCity(City city){
        List<Phrases> phraseByCity = phraseService.getAllPhrasesByCity(city);
        phrase.setItems(phraseByCity);
        phrase.setItemLabelGenerator(Phrases::getName);
    }

    private void createButton(){
        newUserDialog.create.addClickListener(clickEvent -> {
            createNewUser.close();
            editDialog.close();
            UI.getCurrent().navigate(ClientFormView.class);
        });
    }
}



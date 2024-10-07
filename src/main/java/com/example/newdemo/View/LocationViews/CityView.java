package com.example.newdemo.View.LocationViews;

import com.example.newdemo.Entity.State;
import com.example.newdemo.Forms.CityForm;
import com.example.newdemo.Repository.CityRepository;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.Entity.City;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@CssImport("/generated/locationView.css")
@Route(value = "cityView", layout = MainView.class)
public class CityView extends VerticalLayout {

    Grid<City> cityGrid = new Grid<>(City.class, false);
    TextField filterText = new TextField();
    CityForm cityForm;
    CityService cityService;
    StateService stateService;
    CityForm newForm;
    Dialog newFormDialog = new Dialog();
    Dialog editDialog = new Dialog();
    City originalCity;
    @Autowired
    CityRepository cityRepository;

    @Autowired
    public CityView(CityService cityService, StateService stateService){
        this.cityService = cityService;
        this.stateService = stateService;

        cityForm = new CityForm(stateService.getAllStates());

        newForm = new CityForm(stateService.getAllStates());
        newForm.setCity(new City());
        List<State> states = stateService.getAllStates();
        List<State> sortedStates = states.stream()
                .sorted(Comparator.comparing(State::getName))
                .toList();
        newForm.state.setItems(sortedStates);
        newForm.delete.setVisible(false);
        newForm.addSaveListener(this::saveNew);
        newForm.addCloseListener(e -> closeNew());

        newFormDialog.setHeaderTitle("New City");
        newFormDialog.addClassName("titles");

        newFormDialog.getFooter().add(newForm.buttonLayout());
        newFormDialog.add(newForm);

        Tab state = new Tab(new RouterLink("State", StateView.class));
        Tab city = new Tab(new RouterLink("City", CityView.class));
        Tab phrase = new Tab(new RouterLink("Phrase", PhraseView.class));

        state.addClassName("location-items");
        city.addClassName("location-items");
        phrase.addClassName("location-items");

        Tabs locationTabs = new Tabs(state, city, phrase);
        locationTabs.addClassName("location-tabs");

        locationTabs.setSelectedTab(city);
        HorizontalLayout display = new HorizontalLayout(locationTabs);
        display.addClassName("location-navbar");

        setSizeFull();
        configureGrid();
        getToolbar();
        add(display, getToolbar(), cityGrid);
        updateList();
    }


    private void closeNew() {
        newForm.name.clear();
        newForm.cityId.clear();
        newForm.state.clear();
        newForm.setCity(new City());
        newFormDialog.close();
        updateList();
    }

    private void saveNew(CityForm.SaveEvent event){

        String name = event.getCity().getName();
        String id = event.getCity().getCityId();

        Optional<City> cityName = cityRepository.findByName(name);
        Optional<City> cityId = cityRepository.findByCityId(id);

        if(name.isBlank() || id.isBlank()){
            Notification.show("All fields are required", 1500, Notification.Position.BOTTOM_START);
        } else{
            if (cityName.isPresent() && cityId.isPresent()) {
                Notification.show("City and City ID already exist", 1500, Notification.Position.BOTTOM_START);
            } else if (cityName.isPresent()) {
                Notification.show("City already exists", 1500, Notification.Position.BOTTOM_START);
            } else if (cityId.isPresent()) {
                Notification.show("City ID already exists", 1500, Notification.Position.BOTTOM_START);
            } else{
                cityService.saveCity(event.getCity());
              closeNew();
            }
        }
    }

    private void editForm(City city){
        if (city != null) {
            cityForm.setCity(city);

            if(originalCity == null){
                originalCity = new City();
            }

            originalCity.setName(city.getName());
            originalCity.setCityId(city.getCityId());

            cityForm.addSaveListener(this::saveEdit);
            cityForm.addDeleteListener(this::deleteEdit);
            cityForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("City");
            editDialog.addClassName("titles");
            editDialog.getFooter().add(cityForm.buttonLayout());
            editDialog.add(cityForm);
            editDialog.open();
        } else{
            closeEdit();
        }
    }

    private void saveEdit(CityForm.SaveEvent e){
        String name = e.getCity().getName();
        String id = e.getCity().getCityId();

        boolean nameChanged = !name.equals(originalCity.getName());
        boolean cityIdChanged = !id.equals(originalCity.getCityId());

        Optional<City> cityName = cityRepository.findByName(name);
        Optional<City> cityId = cityRepository.findByCityId(id);

        if(nameChanged && cityName.isPresent()){
            Notification.show("City already exist", 1500, Notification.Position.BOTTOM_START);
        } else if(cityIdChanged && cityId.isPresent() ){
            Notification.show("City Id already exist", 1500, Notification.Position.BOTTOM_START);
        } else {
            cityService.saveCity(e.getCity());
            closeEdit();
        }
    }

    private void deleteEdit(CityForm.DeleteEvent event){
        cityService.deleteCity(event.getCity());
        closeEdit();
    }

    private void closeEdit(){
        updateList();
        editDialog.close();
    }


    private void updateList() {
        if(filterText.isEmpty()) {
            List<City> cities = cityService.getAllCitiesByFilter(filterText.getEmptyValue());

            List<City> sortedCities = cities.stream()
                    .sorted(Comparator.comparing(City::getName))
                    .toList();

            cityGrid.setItems(sortedCities);
        } else{
            List<City> cities = cityService.getAllCitiesByFilter(filterText.getValue());

            List<City> sortedCities = cities.stream()
                    .sorted(Comparator.comparing(City::getName))
                    .toList();

            cityGrid.setItems(sortedCities);
        }
    }
        private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setSuffixComponent(new Icon(VaadinIcon.SEARCH));
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addCity  = new Button("Add City", e -> newFormDialog.open());
        addCity.addClassName("add-city-button");

            return new HorizontalLayout(filterText, addCity);
    }

    private void configureGrid() {
        cityGrid.addColumn(city -> city.getState().getName()).setHeader("State");
        cityGrid.addColumn(City::getName).setHeader("City");
        cityGrid.addColumn(City::getCityId).setHeader("City Id");
        cityGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        List<City> city = cityService.getAllCitiesByFilter(filterText.getEmptyValue());
        List<City> sortedCities = city.stream()
                        .sorted(Comparator.comparing(City::getName))
                        .toList();
        cityGrid.setItems(sortedCities);
        cityGrid.addItemClickListener(event -> editForm(event.getItem()));
        cityGrid.addClassName("grid");
    }
}

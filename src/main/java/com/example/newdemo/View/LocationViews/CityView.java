package com.example.newdemo.View.LocationViews;

import com.example.newdemo.Forms.CityForm;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.Entity.City;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "cityView", layout = MainView.class)
public class CityView extends VerticalLayout {

    Grid<City> cityGrid = new Grid<>(City.class, false);
    TextField filterText = new TextField();

    CityForm cityForm;
    Dialog newCityDialog = new Dialog();
    CityService cityService;
    StateService stateService;

    CityForm newForm;
    Dialog newFormDialog = new Dialog();
    Dialog editDialog = new Dialog();

    @Autowired
    public CityView(CityService cityService, StateService stateService){
        this.cityService = cityService;
        this.stateService = stateService;

        cityForm = new CityForm(stateService.getAllStates());
        newCityDialog.add(cityForm);

        newForm = new CityForm(stateService.getAllStates());
        newForm.setCity(new City());
        newForm.delete.setVisible(false);
        newForm.addSaveListener(this::saveNew);
        newForm.addCloseListener(e -> closeNew());

        newFormDialog.setHeaderTitle("New Form");
        newFormDialog.getFooter().add(newForm.buttonLayout());
        newFormDialog.add(newForm);

        Tab state = new Tab(new RouterLink("State", StateView.class));
        Tab city = new Tab(new RouterLink("City", CityView.class));

        HorizontalLayout display = new HorizontalLayout(state, city);

        Scroller item = new Scroller(display);

        setSizeFull();
        configureGrid();
        configureForm();
        getToolbar();
        add(item, getToolbar(), cityGrid);
        updateList();
    }

    private void saveNew(CityForm.SaveEvent event) {
        cityService.saveCity(event.getCity());
        updateList();
        closeNew();
    }

    private void closeNew() {
        newFormDialog.close();
    }

    private void save(CityForm.SaveEvent event){
        cityService.saveCity(event.getCity());
        updateList();
        close();
    }

    private void delete(CityForm.DeleteEvent event){
        cityService.deleteCity(event.getCity());
        updateList();
        close();
    }
    private void close(){
        newCityDialog.close();
    }

    private void saveEdit(CityForm.SaveEvent event){
        cityService.saveCity(event.getCity());
        updateList();
        closeEdit();
    }

    private void deleteEdit(CityForm.DeleteEvent event){
        cityService.deleteCity(event.getCity());
        updateList();
        closeEdit();
    }

    private void closeEdit(){
        editDialog.close();
    }

    private void editForm(City city){
        if (city != null) {
            cityForm.setCity(city);

            cityForm.addSaveListener(this::saveEdit);
            cityForm.addDeleteListener(this::deleteEdit);
            cityForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("City");
            editDialog.getFooter().add(cityForm.buttonLayout());
            editDialog.add(cityForm);
            editDialog.open();
        } else{
            closeEdit();
        }
    }
    private void configureForm() {
        cityForm = new CityForm(stateService.getAllStates());

        cityForm.addSaveListener(this::save);
        cityForm.addDeleteListener(this::delete);
        cityForm.addCloseListener(e -> close());

        newCityDialog.setHeaderTitle("City");
        newCityDialog.getFooter().add(cityForm.buttonLayout());
    }

    private void updateList() {
        cityGrid.setItems(cityService.getAllCitiesByFilter(filterText.getValue()));
    }

    private HorizontalLayout getToolbar() {
        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setSuffixComponent(searchIcon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addCity  = new Button("Add City", e -> newFormDialog.open());

        var toolbar = new HorizontalLayout(filterText, addCity);
        toolbar.addClassName("CityToolBar");
        return toolbar;
    }

    private void configureGrid() {
        cityGrid.addColumn(city -> city.getState().getName()).setHeader("State");
        cityGrid.addColumn(City::getName).setHeader("City");
        cityGrid.addColumn(City::getCityId).setHeader("City Id");
        cityGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        List<City> city =cityService.getAllCitiesByFilter(filterText.getEmptyValue());
        cityGrid.setItems(city);

        cityGrid.asSingleSelect().addValueChangeListener(event -> editForm(event.getValue()));

    }
}

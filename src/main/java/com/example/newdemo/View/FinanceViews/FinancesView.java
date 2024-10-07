package com.example.newdemo.View.FinanceViews;


import com.example.newdemo.Entity.*;
import com.example.newdemo.Forms.FinanceForm;
import com.example.newdemo.Service.*;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@CssImport("/generated/finances.css")
@Route(value = "financesView", layout = MainView.class)
public class FinancesView extends VerticalLayout implements RouterLayout {
    TextField filterText = new TextField();
    Grid<Finances> recordGrid = new Grid<>(Finances.class, false);
    FinanceForm newForm;
    UserService userService;
    PropertyService propertyService;
    FinanceService financeService;
    StateService stateService;
    CityService cityService;
    PhraseService phraseService;
    Dialog newFormDialog = new Dialog();
    Users selectedUser;
    State selectedState;
    City selectedCity;
    Phrases selectedPhrases;
    Property.PropertyType selectedType;
    Double price;

    @Autowired
    public FinancesView(UserService userService, FinanceService financeService, PropertyService propertyService,
                        StateService stateService, CityService cityService, PhraseService phraseService){
        this.userService = userService;
        this.financeService = financeService;
        this.propertyService = propertyService;
        this.stateService = stateService;
        this.cityService = cityService;
        this.phraseService = phraseService;

        newForm = new FinanceForm(userService.findUserNamesByClientUserRole(), stateService.getAllStates(), cityService.getAllCities(),
                phraseService.getAllPhrases());
        newForm.setFinances(new Finances());

        newFormDialog.add(newForm);
        newFormDialog.getFooter().add(newForm.financeButtonLayout());
        newFormDialog.setHeaderTitle("New Record");

        List<Users> users = userService.findUserNamesByClientUserRole();
        List<Users> sortedNames = users.stream()
                .sorted(Comparator.comparing(Users::toString))
                .toList();

        newForm.owner.setItems(sortedNames);
        newForm.owner.setItemLabelGenerator(Users::toString);
        newForm.state.setItems(sortedState());
        newForm.city.setItems(sortedCity());
        newForm.phrase.setItems(sortedPhrase());

        newForm.type.setEnabled(false);
        newForm.state.setEnabled(false);
        newForm.city.setEnabled(false);
        newForm.phrase.setEnabled(false);


        newForm.owner.addValueChangeListener(event -> {
            selectedUser = event.getValue();
            getStateForUser(selectedUser);
            getCityForUser(selectedUser);
            getPhraseFOrUser(selectedUser);
            newForm.state.setEnabled(true);
        });

        newForm.state.addValueChangeListener(e -> {
            selectedState = e.getValue();
            newForm.city.setEnabled(true);
        });

        newForm.city.addValueChangeListener(e -> {
            selectedCity = e.getValue();
            newForm.phrase.setEnabled(true);
        });

        newForm.phrase.addValueChangeListener(e -> {
            selectedPhrases = e.getValue();
            getPropertyTypeForUser(selectedUser, selectedState, selectedCity, selectedPhrases);
            newForm.type.setEnabled(true);
        });
        newForm.type.addValueChangeListener(e -> selectedType = e.getValue());

        newForm.addSaveListener(this::saveNew);
        newForm.addCloseListener(e -> newFormDialog.close());

        setSizeFull();
        configureGrid();
        getToolbar();

        add(getToolbar(), recordGrid);
    }

        private void configureGrid(){
            recordGrid.addColumn(Finances::getOwner).setHeader("Owner");
            recordGrid.addColumn(Finances::getType).setHeader("Property Type");
            recordGrid.addColumn(Finances::getOutstandingFormattedToString).setHeader("Outstanding Amount").addClassName("outstanding");
            recordGrid.addColumn(Finances::getPaidBy).setHeader("Paid By");
            recordGrid.addColumn(Finances::getAmountPaidFormattedToString).setHeader("Amount Paid");
            recordGrid.addColumn(Finances::getDate).setHeader("Date");

            List<Finances> financesList = financeService.getAllRecords();
            recordGrid.setItems(financesList);

            updateList();
            recordGrid.addClassName("grid");
        }

        private void getPropertyTypeForUser(Users user, State state, City city, Phrases phrases){
           List<Property.PropertyType> typeByUser = propertyService.findAllPropertyTypesForUser(user, state, city, phrases);
           newForm.type.setItems(typeByUser);
        }

        private void getStateForUser(Users user){
            List<State> stateForUser = stateService.findAllPropertyByStateThroughUser(user);
        }

        private void getCityForUser(Users user){
         List<City> cityList = cityService.findAllCitiesOfPropertyByUser(user);
        }

        private void getPhraseFOrUser(Users user){
        List<Phrases> phrasesList = phraseService.findAllPhrasesOfPropertyByUser(user);
        }

        private double getPriceThroughUser(Users user, Property.PropertyType type){
            return propertyService.findPropertyPriceByUserAndType(user, type);
        }

        private HorizontalLayout getToolbar(){
           Button addNewRecord = new Button("New Record", e -> newFormDialog.open());
           addNewRecord.addClassName("add-finance-button");
           addNewRecord.setPrefixComponent(new Icon(VaadinIcon.PLUS));
            filterText.setPlaceholder("Search");
            filterText.setClearButtonVisible(true);
            filterText.addClassName("filter-text");
            filterText.setSuffixComponent(new Icon(VaadinIcon.SEARCH));
            filterText.setValueChangeMode(ValueChangeMode.LAZY);
            filterText.addValueChangeListener(e -> updateList());

            return new HorizontalLayout(filterText, addNewRecord);
        }

        private void saveNew(FinanceForm.SaveEvent event){
            Finances lastRecord = financeService.getLastRecordForUser(selectedUser, selectedState, selectedCity, selectedPhrases, selectedType);

            if(lastRecord == null){
                price = getPriceThroughUser(selectedUser, selectedType);
                event.getFinances().setPrice(price);
            }
            else{
                price = lastRecord.getOutstandingAmount();
                event.getFinances().setPrice(price);
            }

            event.getFinances().setDate(LocalDate.now());
            event.getFinances().setDateTime(LocalDateTime.now());
            event.getFinances().setType(selectedType);
            event.getFinances().setOwner(selectedUser);
            event.getFinances().setOutstandingAmount(price - event.getFinances().getAmountPaid());

            financeService.saveFinanceRecords(event.getFinances());
            clearingDetails();
            updateList();
        }

        private void clearingDetails(){
            newForm.owner.clear();
            newForm.type.clear();
            newForm.state.clear();
            newForm.city.clear();
            newForm.phrase.clear();
            newForm.amountPaid.clear();
            newForm.paidBy.clear();
            newForm.state.setEnabled(false);
            newForm.city.setEnabled(false);
            newForm.phrase.setEnabled(false);
            newForm.type.setEnabled(false);
            newForm.setFinances(new Finances());
            newFormDialog.close();
        }

        private void updateList(){
        List<Finances> record = financeService.searchFinancialRecordsByUserToString(filterText.getValue());
            recordGrid.setItems(record);
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

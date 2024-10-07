package com.example.newdemo.View.LocationViews;


import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phrases;
import com.example.newdemo.Entity.State;
import com.example.newdemo.Forms.PhraseForm;
import com.example.newdemo.Repository.PhraseRepository;
import com.example.newdemo.Service.CityService;
import com.example.newdemo.Service.PhraseService;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.View.MainView;
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


@CssImport("/generated/phrase.css")
@Route(value = "phraseView", layout = MainView.class)
public class PhraseView extends VerticalLayout {

    Grid<Phrases> phrasesGrid = new Grid<>(Phrases.class, false);
    TextField filterText = new TextField();
    PhraseForm phraseNewForm;
    Phrases originalPhrase;
    PhraseForm editPhraseForm;
    CityService cityService;
    StateService stateService;
    PhraseService service;
    Dialog newFormDialog = new Dialog();
    Dialog editDialog = new Dialog();
    ComboBox<State> state = new ComboBox<>("State");
    ComboBox<City> city = new ComboBox<>("City");
    Button resetFilterButton = new Button("ResetFilters");

    @Autowired
    PhraseRepository phraseRepository;

    @Autowired
    public PhraseView(StateService stateService, CityService cityService, PhraseService service){
        this.stateService = stateService;
        this.cityService =  cityService;
        this.service = service;

        editPhraseForm = new PhraseForm(stateService.getAllStates(), cityService.getAllCities());
        phraseNewForm = new PhraseForm(stateService.getAllStates(), cityService.getAllCities());
        phraseNewForm.setPhrases(new Phrases());

        phraseNewForm.state.setItems(sortedState());
        phraseNewForm.city.setItems(sortedCity());

        phraseNewForm.city.setEnabled(false);
        phraseNewForm.state.addValueChangeListener(e -> {
            State selectedState = e.getValue();
            getCityListByState(selectedState);
            phraseNewForm.city.setEnabled(true);
        });

        phraseNewForm.delete.setVisible(false);
        phraseNewForm.addSaveListener(this::saveNew);
        phraseNewForm.addCloseListener(e -> closeNew());

        newFormDialog.setHeaderTitle("New Phrase");
        newFormDialog.addClassName("titles");
        newFormDialog.getFooter().add(phraseNewForm.buttonLayout());
        newFormDialog.add(phraseNewForm);

        HorizontalLayout display = getHorizontalLayout();

        setSizeFull();
        configureGrid();
        getToolbar();
        add(display, getToolbar(), phrasesGrid);
        updateList();
    }

    private static HorizontalLayout getHorizontalLayout() {
        Tab state = new Tab(new RouterLink("State", StateView.class));
        Tab city = new Tab(new RouterLink("City", CityView.class));
        Tab phrase = new Tab(new RouterLink("Phrase", PhraseView.class));

        state.addClassName("location-items");
        city.addClassName("location-items");
        phrase.addClassName("location-items");

        Tabs locationTabs = new Tabs(state, city, phrase);
        locationTabs.addClassName("location-tabs");

        locationTabs.setSelectedTab(phrase);
        HorizontalLayout display = new HorizontalLayout(locationTabs);
        display.addClassName("location-navbar");
        return display;
    }

    private void saveNew(PhraseForm.SaveEvent e){
        String name = e.getPhrases().getName();
        String id = e.getPhrases().getPhraseId();

        Optional<Phrases> phraseName = phraseRepository.findByName(name);
        Optional<Phrases> phraseId = phraseRepository.findByPhraseId(id);

        if (name.isBlank() || id.isBlank()) {
            Notification.show("All fields are required", 1500, Notification.Position.BOTTOM_START);
        } else {
            if (phraseName.isPresent() && phraseId.isPresent()) {
                Notification.show("Phrase and Phrase Id already exist", 1500, Notification.Position.BOTTOM_START);
            } else if (phraseName.isPresent()) {
                Notification.show("Phrase already exist", 1500, Notification.Position.BOTTOM_START);
            } else if (phraseId.isPresent()) {
                Notification.show("Phrase Id already exist", 1500, Notification.Position.BOTTOM_START);
            } else {
                service.savePhrases(e.getPhrases());
                closeNew();
            }
        }
    }
    private void closeNew() {
        phraseNewForm.state.clear();
        phraseNewForm.city.clear();
        phraseNewForm.city.setEnabled(false);
        phraseNewForm.name.clear();
        phraseNewForm.phraseId.clear();
        phraseNewForm.setPhrases(new Phrases());
        newFormDialog.close();
        updateList();
    }

    private HorizontalLayout getToolbar(){
        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setSuffixComponent(searchIcon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.addClassName("filter-text");

        Button addPhrase  = new Button("Add Phrases", e -> newFormDialog.open());
        addPhrase.setPrefixComponent(new Icon(VaadinIcon.PLUS));
        addPhrase.addClassName("add-phrase-button");

        state.setItems(sortedState());
        state.setItemLabelGenerator(State::getName);

        city.setItems(sortedCity());
        city.setItemLabelGenerator(City::getName);

        state.addValueChangeListener(e -> {
            State selectedState = e.getValue();
            getCityListByState(selectedState);
        });
        city.setEnabled(false);

        state.addValueChangeListener(e ->{
            city.setEnabled(true);
            performSearch();
        });

        city.addValueChangeListener(e -> performSearch());

        resetFilterButton.addClickListener(clickEvent -> {
            state.clear();
            city.clear();
            city.setEnabled(false);
            updateList();
        });

        resetFilterButton.addClassName("reset-filters");

        return new HorizontalLayout(addPhrase, filterText, state, city, resetFilterButton);
    }

    private void performSearch() {
        State selectedState = state.getValue();
        City selectedCity = city.getValue();

        List<Phrases> searchResults = phraseRepository.searchByStateAndCity(selectedState, selectedCity);
        List<Phrases> sortedPhrases = searchResults.stream()
                .sorted(Comparator.comparing(Phrases::getName))
                .toList();
        phrasesGrid.setItems(sortedPhrases);
    }

    private void getCityListByState(State state){
        List<City> cityByState = cityService.getAllCitiesByStateByList(state);
        phraseNewForm.city.setItems(cityByState);
        phraseNewForm.city.setItemLabelGenerator(City::getName);
        city.setItems(cityByState);
        city.setItemLabelGenerator(City::getName);
    }

    private void updateList(){
        if(filterText.isEmpty()){
            List<Phrases> phrases = service.getAllPhrasesByFilter(filterText.getEmptyValue());

            List<Phrases> sortedPhrases = phrases.stream()
                    .sorted(Comparator.comparing(Phrases::getName))
                    .toList();

            phrasesGrid.setItems(sortedPhrases);
        } else{
            List<Phrases> phrases = service.getAllPhrasesByFilter(filterText.getValue());

            List<Phrases> sortedPhrases = phrases.stream()
                    .sorted(Comparator.comparing(Phrases::getName))
                    .toList();

            phrasesGrid.setItems(sortedPhrases);
        }
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

    private void configureGrid(){
        phrasesGrid.addColumn(phrases -> phrases.getState().getName()).setHeader("State");
        phrasesGrid.addColumn(phrases -> phrases.getCity().getName()).setHeader("City");
        phrasesGrid.addColumn(Phrases::getName).setHeader("Phrase");
        phrasesGrid.addColumn(Phrases::getPhraseId).setHeader("Phrase Id");
        phrasesGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        List<Phrases> phrases = service.getAllPhrasesByFilter(filterText.getEmptyValue());
        List<Phrases> sortedPhrases = phrases.stream()
                .sorted(Comparator.comparing(Phrases::getName))
                .toList();
        phrasesGrid.setItems(sortedPhrases);

        phrasesGrid.addItemClickListener(e -> editForm(e.getItem()));
        phrasesGrid.addClassName("grid");
    }

    private void editForm(Phrases phrases){
        if(phrases != null){
            editPhraseForm.setPhrases(phrases);

            if(originalPhrase == null){
                originalPhrase = new Phrases();
            }

            originalPhrase.setName(phrases.getName());
            originalPhrase.setPhraseId(phrases.getPhraseId());

            editPhraseForm.addSaveListener(this::saveEdit);
            editPhraseForm.addDeleteListener(this::deleteEdit);
            editPhraseForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("Phrase");
            editDialog.addClassName("titles");
            editDialog.getFooter().add(editPhraseForm.buttonLayout());
            editDialog.add(editPhraseForm);
            editDialog.open();
        } else{
            closeEdit();
        }
    }

    private void saveEdit(PhraseForm.SaveEvent e) {
        String name = e.getPhrases().getName();
        String id = e.getPhrases().getPhraseId();

        boolean nameChanged = !name.equals(originalPhrase.getName());
        boolean phraseIdChanged = !id.equals(originalPhrase.getPhraseId());

        Optional<Phrases> phraseName = phraseRepository.findByName(name);
        Optional<Phrases> phraseId = phraseRepository.findByPhraseId(id);

            if (nameChanged && phraseName.isPresent()) {
                Notification.show("Phrase already exist", 1500, Notification.Position.BOTTOM_START);
            } else if (phraseIdChanged && phraseId.isPresent()) {
                Notification.show("Phrase Id already exist", 1500, Notification.Position.BOTTOM_START);
            } else {
                service.savePhrases(e.getPhrases());
                updateList();
                closeEdit();
            }
    }
    
    private void deleteEdit(PhraseForm.DeleteEvent e){
        service.deletePhrases(e.getPhrases());
        closeEdit();
    }

    private void closeEdit(){
        updateList();
        editDialog.close();
    }
}

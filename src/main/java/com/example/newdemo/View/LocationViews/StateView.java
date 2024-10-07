package com.example.newdemo.View.LocationViews;

import com.example.newdemo.Forms.StateForm;
import com.example.newdemo.Repository.StateRepository;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.Entity.State;
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
import java.util.stream.Collectors;

@CssImport("/generated/locationView.css")
@Route(value = "stateView", layout = MainView.class)
public class StateView extends VerticalLayout {

    Grid<State> stateGrid = new Grid<>(State.class);
    TextField filterText = new TextField();
    StateForm editStateForm  = new StateForm();
    StateService service;
    State originalState;
    StateForm newStateForm = new StateForm();
    Dialog editDialog = new Dialog();
    Dialog newStateDialog = new Dialog();
    @Autowired
    StateRepository stateRepository;

    @Autowired
    public StateView(StateService service) {
        this.service = service;

        setSizeFull();
        configureGrid();

        newStateForm.setState(new State());
        newStateForm.delete.setVisible(false);

        newStateForm.addSaveListener(this::saveNew);
        newStateForm.addCloseListener(e -> closeNew());

        newStateDialog.setHeaderTitle("New State");
        newStateDialog.addClassName("titles");

        newStateDialog.getFooter().add(newStateForm.buttonLayout());
        newStateDialog.add(newStateForm);

        Tab state = new Tab(new RouterLink("State", StateView.class));
        Tab city = new Tab(new RouterLink("City", CityView.class));
        Tab phrase = new Tab(new RouterLink("Phrase", PhraseView.class));

        state.addClassName("location-items");
        city.addClassName("location-items");
        phrase.addClassName("location-items");


        Tabs locationTabs = new Tabs(state, city, phrase);
        locationTabs.addClassName("location-tabs");

        locationTabs.setSelectedTab(state);
        HorizontalLayout display = new HorizontalLayout(locationTabs);
        display.addClassName("location-navbar");
        getToolbar();

        add(display, getToolbar(), stateGrid);
        updateList();
    }

    private void updateList() {
       if(filterText.isEmpty()) {
           List<State> states = service.getAllStatesByFilter(filterText.getEmptyValue());

           List<State> sortedStates = states.stream()
                   .sorted(Comparator.comparing(State::getName))
                   .collect(Collectors.toList());

           stateGrid.setItems(sortedStates);
           stateGrid.setColumns("name", "stateId");
       } else{
           List<State> states = service.getAllStatesByFilter(filterText.getValue());

           List<State> sortedStates = states.stream()
                   .sorted(Comparator.comparing(State::getName))
                   .toList();

           stateGrid.setItems(sortedStates);
       }
    }

    private void saveNew(StateForm.SaveEvent event) {
        String name = event.getState().getName();
        String id = event.getState().getStateId();

        Optional<State> stateName = stateRepository.findByName(name);
        Optional<State> stateId = stateRepository.findByStateId(id);

        if(name.isBlank() || id.isBlank()){
            Notification.show("All fields are required", 1500, Notification.Position.BOTTOM_START);
        } else{
            if (stateName.isPresent() && stateId.isPresent()) {
                Notification.show("State and State ID already exist", 1500, Notification.Position.BOTTOM_START);
            } else if (stateName.isPresent()) {
                Notification.show("State already exists", 1500, Notification.Position.BOTTOM_START);
            } else if (stateId.isPresent()) {
                Notification.show("State ID already exists", 1500, Notification.Position.BOTTOM_START);
            }
            else {
                service.saveState(event.getState());
                closeNew();
            }
        }
    }
    private void closeNew(){
        newStateForm.name.clear();
        newStateForm.stateId.clear();
        newStateForm.setState(new State());
        newStateDialog.close();
        updateList();
    }


    private void configureGrid() {
      List<State> states = service.getAllStatesByFilter(filterText.getEmptyValue());
        List<State> sortedStates = states.stream()
                .sorted(Comparator.comparing(State::getName))
                .collect(Collectors.toList());

        stateGrid.setItems(sortedStates);
        stateGrid.setColumns("name", "stateId");
        stateGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        stateGrid.addItemClickListener(event -> edit(event.getItem()));
        stateGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        stateGrid.addItemClickListener(event -> edit(event.getItem()));
        stateGrid.addClassName("grid");
    }


    private void edit(State state){
        if(state != null){
            editStateForm.setState(state);

            if (originalState == null) {
                originalState = new State();
            }

            originalState.setName(state.getName());
            originalState.setStateId(state.getStateId());

            editStateForm.addSaveListener(this::saveEdit);
            editStateForm.addDeleteListener(this::deleteEdit);
            editStateForm.addCloseListener(e -> closeEdit());

            editDialog.setHeaderTitle("State");
            editDialog.addClassName("titles");
            editDialog.getFooter().add(editStateForm.buttonLayout());
            editDialog.add(editStateForm);
            editDialog.open();
        }  else{
            closeEdit();
        }
    }

    private void saveEdit(StateForm.SaveEvent e) {
        String name = e.getState().getName();
        String id = e.getState().getStateId();

        boolean nameChanged = !name.equals(originalState.getName());
        boolean stateIdChanged = !id.equals(originalState.getStateId());

        Optional<State> stateName = stateRepository.findByName(name);
        Optional<State> stateId = stateRepository.findByStateId(id);

        if(nameChanged && stateName.isPresent()) {
            Notification.show("State already exist", 3000, Notification.Position.BOTTOM_START);
        }
        else if(stateIdChanged && stateId.isPresent()) {
            Notification.show("State Id already exist", 3000, Notification.Position.BOTTOM_START);
        } else{
            service.saveState(e.getState());
            updateList();
            closeEdit();
        }
    }

    private void closeEdit(){
        editDialog .close();
    }

    private void deleteEdit(StateForm.DeleteEvent event){
        service.deleteState(event.getState());
        updateList();
        closeEdit();
    }

    private HorizontalLayout getToolbar() {
        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setSuffixComponent(searchIcon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addState = new Button("Add State", clickEvent -> newStateDialog.open());
        addState.addClassName("add-state-button");
        HorizontalLayout toolbar = new HorizontalLayout(filterText, addState);
        return toolbar;
    }


}

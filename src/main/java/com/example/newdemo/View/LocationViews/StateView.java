package com.example.newdemo.View.LocationViews;

import com.example.newdemo.Forms.StateForm;
import com.example.newdemo.Service.StateService;
import com.example.newdemo.Entity.State;
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


@Route(value = "", layout = MainView.class)
public class StateView extends VerticalLayout {

    Grid<State> stateGrid = new Grid<>(State.class);

    TextField filterText = new TextField();

    StateForm stateForm = new StateForm();
    Dialog stateFormDialog = new Dialog();
    StateService service;
    StateForm newStateForm = new StateForm();
    Dialog newDialog = new Dialog();
    Dialog newStateDialog = new Dialog();

    @Autowired
    public StateView(StateService service) {
        this.service = service;

        setSizeFull();
        configureGrid();

        stateFormDialog.add(stateForm);

        newStateForm.setState(new State());
        newStateForm.delete.setVisible(false);

        newStateForm.addSaveListener(this::saveNew);
        newStateForm.addCloseListener(e -> closeNew());

        newStateDialog.setHeaderTitle("New State");
        newStateDialog.getFooter().add(newStateForm.buttonLayout());
        newStateDialog.add(newStateForm);

        Tab state = new Tab(new RouterLink("State", StateView.class));
        Tab city = new Tab(new RouterLink("City", CityView.class));

        HorizontalLayout display = new HorizontalLayout(state, city);

        Scroller item = new Scroller(display);

        configureForm();
        getToolbar();

        add(item, getToolbar(), stateGrid);
        updateList();
    }


    private void updateList() {
        stateGrid.setItems(service.getAllStatesByFilter(filterText.getValue()));
    }

    private void save(StateForm.SaveEvent event){
        service.saveState(event.getState());
        updateList();
        close();
    }

    private void delete(StateForm.DeleteEvent event){
        service.deleteState(event.getState());
        updateList();
        close();
    }
    private void close(){
       stateFormDialog.close();
    }

    private void configureGrid() {
        stateGrid.setItems(service.getAllStatesByFilter(filterText.getEmptyValue()));
        stateGrid.setColumns("name", "stateId");
        stateGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        stateGrid.asSingleSelect().addValueChangeListener(event -> edit(event.getValue()));
    }

    private void configureForm() {
        stateForm = new StateForm();

        stateForm.addSaveListener(this::save);
        stateForm.addDeleteListener(this::delete);
        stateForm.addCloseListener(e -> close());

        stateFormDialog.setHeaderTitle("State");
        stateFormDialog.getFooter().add(stateForm.buttonLayout());
    }

    private void saveEdit(StateForm.SaveEvent event){
        service.saveState(event.getState());
        updateList();
        closeEdit();
    }


    private void closeEdit(){
        newDialog.close();
    }

    private void deleteEdit(StateForm.DeleteEvent event){
        service.deleteState(event.getState());
        updateList();
        closeEdit();
    }

    private void edit(State state){
        if(state != null){
            stateForm.setState(state);

            stateForm.addSaveListener(this::saveEdit);
            stateForm.addDeleteListener(this::deleteEdit);
            stateForm.addCloseListener(e -> closeEdit());


            newDialog.setHeaderTitle("State");
            newDialog.getFooter().add(stateForm.buttonLayout());
            newDialog.add(stateForm);
            newDialog.open();
        }  else{
            closeEdit();
        }
    }

    private void saveNew(StateForm.SaveEvent event){
        service.saveState(event.getState());
        updateList();
        closeNew();

        newStateForm.setState(new State());
    }

    private void closeNew(){
        newStateDialog.close();
    }

    private HorizontalLayout getToolbar() {
        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setSuffixComponent(searchIcon);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

       Button addState = new Button("Add State", event -> newStateDialog.open());


        var toolbar = new HorizontalLayout(filterText, addState);
        toolbar.addClassName("stateToolBar");
        return toolbar;
    }


}

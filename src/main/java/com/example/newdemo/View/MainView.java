package com.example.newdemo.View;

import com.example.newdemo.View.FinanceViews.FinancesView;
import com.example.newdemo.View.LocationViews.StateView;
import com.example.newdemo.View.PropertyViews.PropertyView;
import com.example.newdemo.View.UserViews.UserView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;


@CssImport("/generated/mainview.css")
public class MainView extends AppLayout {
    public MainView(){

        DrawerToggle toggle = new DrawerToggle();

        Tab location = new Tab(VaadinIcon.LOCATION_ARROW.create(), new RouterLink("Location", StateView.class));
        Tab property = new Tab(VaadinIcon.WORKPLACE.create(), new RouterLink("Properties", PropertyView.class));
        Tab user = new Tab(VaadinIcon.USERS.create(), new RouterLink("Users", UserView.class));
        Tab finance = new Tab(VaadinIcon.BAR_CHART.create(),new RouterLink("Finances", FinancesView.class));

        VerticalLayout display = new VerticalLayout(location, property, user, finance);

        location.addClassName("Tabs");
        property.addClassName("Tabs");
        user.addClassName("Tabs");
        finance.addClassName("Tabs");
        user.getStyle().set("padding-right", "130px");


        Scroller items = new Scroller(display);

        items.addClassName("MainDisplay");
        items.getStyle().set("color", "white");

        addToDrawer(items);
        addToNavbar(toggle);

        setPrimarySection(Section.DRAWER);
    }

}

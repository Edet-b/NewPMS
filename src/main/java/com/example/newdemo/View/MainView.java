package com.example.newdemo.View;

import com.example.newdemo.View.FinanceViews.FinancesView;
import com.example.newdemo.View.LocationViews.StateView;
import com.example.newdemo.View.PropertyViews.PropertyView;
import com.example.newdemo.View.UserViews.ClientView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;


@CssImport("/generated/mainView.css")
public class MainView extends AppLayout {
    Image image = new Image();
    public MainView(){
        DrawerToggle toggle = new DrawerToggle();

        SideNav nav = new SideNav();

        VerticalLayout imageLayout = new VerticalLayout();
        imageLayout.setWidthFull();

        image = new Image("images/logo.png", "Logo");
        image.getStyle().setHeight("80%").setWidth("80%");

        imageLayout.add(image);
        imageLayout.getStyle().set("background-color", "#1434A4").set("color", "white");


        SideNavItem dashboard = new SideNavItem("Dashboard", Dashboard.class, VaadinIcon.DASHBOARD.create());
        SideNavItem location = new SideNavItem("Location", StateView.class, VaadinIcon.LOCATION_ARROW.create());
        SideNavItem property = new SideNavItem("Properties", PropertyView.class, VaadinIcon.WORKPLACE.create());

        SideNavItem users = new SideNavItem("Users", ClientView.class, VaadinIcon.USERS.create());
        SideNavItem finances = new SideNavItem("Finances", FinancesView.class, VaadinIcon.BAR_CHART.create());

        nav.addItem(dashboard, location, property, users, finances);

        dashboard.addClassName("tabs");
        location.addClassName("tabs");
        property.addClassName("tabs");
        users.addClassName("tabs");
        finances.addClassName("tabs");

        nav.setWidthFull();
        Div navWrapper = new Div(nav);

        navWrapper.addClassName("sideNavbar");
        navWrapper.setMaxHeight("100%");

        addToDrawer(imageLayout, navWrapper);
        addToNavbar(toggle);

        setPrimarySection(Section.DRAWER);
    }

}

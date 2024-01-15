package com.example.newdemo.View;

import com.example.newdemo.Entity.Users;
import com.example.newdemo.Service.PropertyService;
import com.example.newdemo.Service.UserService;
import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;


@CssImport("/generated/dashboard.css")
@Route(value = "dashboard", layout = MainView.class)
public class Dashboard extends VerticalLayout {

    PropertyService propertyService;

    UserService userService;

    @Autowired
    public Dashboard(PropertyService propertyService, UserService userService){
        this.propertyService = propertyService;
        this.userService = userService;

        VerticalLayout mainLayout = new VerticalLayout();

        HorizontalLayout topLayout = new HorizontalLayout();
        HorizontalLayout topLayoutTwo = new HorizontalLayout();

        //Card One
        VerticalLayout cardForAllProperties = new VerticalLayout();
        cardForAllProperties.addClassNames("card-for-all-properties", "total-clients");

        HorizontalLayout totalProperties = new HorizontalLayout();
        totalProperties.add(new Icon(VaadinIcon.BUILDING_O), new Text("Total Properties"));
        totalProperties.addClassName("text-of-cards");

        Long totalPropertiesCount = propertyService.totalProperties();
        HorizontalLayout totalPropertiesCounts = new HorizontalLayout();
        totalPropertiesCounts.getStyle().set("font-size", "35px");
        totalPropertiesCounts.add(String.valueOf(totalPropertiesCount));

        totalPropertiesCounts.getStyle().set("color", "white");
        totalProperties.getStyle().set("color", "white");
        cardForAllProperties.getStyle().set("background-color", "blue");


        cardForAllProperties.add(totalPropertiesCounts, totalProperties);

        // Card Two
        VerticalLayout cardForAllLandedProperties = new VerticalLayout();
        cardForAllLandedProperties.addClassNames("card-for-all-landed-properties", "total-clients");

        HorizontalLayout totalLandedProperties = new HorizontalLayout();
        totalLandedProperties.add(new Icon(VaadinIcon.BUILDING), new Text("Total Land Properties"));
        totalLandedProperties.addClassName("text-of-cards");

        Long totalLandedPropertiesCount = propertyService.totalPropertiesByLand();
        HorizontalLayout totalLandedPropertiesCounts = new HorizontalLayout();
        totalLandedPropertiesCounts.getStyle().set("font-size", "35px");

        totalLandedPropertiesCounts.add(String.valueOf(totalLandedPropertiesCount));

        totalLandedProperties.getStyle().set("color", "white");
        totalLandedPropertiesCounts.getStyle().set("color", "white");
        cardForAllLandedProperties.getStyle().set("background-color", "green");
        cardForAllLandedProperties.add(totalLandedPropertiesCounts, totalLandedProperties);

        // Card Three
        VerticalLayout cardForResidentialProperties = new VerticalLayout();
        cardForResidentialProperties.addClassNames("card-for-all-residential-properties", "total-clients");

        HorizontalLayout totalResidentialProperties = new HorizontalLayout();
        totalResidentialProperties.add(new Icon(VaadinIcon.BUILDING_O), new Text("Total Residential Properties"));
        totalResidentialProperties.addClassName("text-of-cards");


        Long totalResidentialPropertiesCount = propertyService.totalOtherProperties();
        HorizontalLayout totalResidentialPropertiesCounts = new HorizontalLayout();
        totalResidentialPropertiesCounts.getStyle().set("font-size", "35px");

        totalResidentialPropertiesCounts.add(String.valueOf(totalResidentialPropertiesCount));

        totalResidentialProperties.getStyle().set("color", "white");
        totalResidentialPropertiesCounts.getStyle().set("color", "white");
        cardForResidentialProperties.getStyle().set("background-color", "orange");
        cardForResidentialProperties.add(totalResidentialPropertiesCounts, totalResidentialProperties);

        //Card Four

        VerticalLayout cardForClients = new VerticalLayout();
        cardForClients.addClassNames("card-for-clients","total-clients" );

        HorizontalLayout totalClients = new HorizontalLayout();
        totalClients.add(new Icon(VaadinIcon.USERS), new Text("Total Clients"));
        totalClients.addClassName("text-of-cards");
//        totalClients.addClassName("total-clients");

        Long totalClientsCount = userService.findUserByUserRoles();
        HorizontalLayout totalClientsCounts = new HorizontalLayout();
        totalClientsCounts.getStyle().set("font-size", "35px");
        
        totalClientsCounts.add(String.valueOf(totalClientsCount));

        totalClients.getStyle().set("color", "white");
        totalClientsCounts.getStyle().set("color", "white");
        cardForClients.getStyle().set("background-color", "purple");

        cardForClients.add(totalClientsCounts, totalClients);


        //Card Five
        VerticalLayout cardForStaff = new VerticalLayout();
        cardForStaff.addClassNames("card-for-staff", "total-clients");

        HorizontalLayout totalStaff = new HorizontalLayout();
        totalStaff.add(new Icon(VaadinIcon.USERS),new Text("Total Staff"));

        Long totalStaffCount = userService.findOtherUserRolesExceptClients();
        HorizontalLayout totalStaffCounts = new HorizontalLayout();
        totalStaff.addClassName("text-of-cards");
        totalStaffCounts.addClassName("total-clients");
        totalStaffCounts.getStyle().set("font-size", "35px");

        totalStaffCounts.add(String.valueOf(totalStaffCount));

        totalStaff.getStyle().set("color", "white");
        totalStaffCounts.getStyle().set("color", "white");
        cardForStaff.getStyle().set("background-color", "grey");

        cardForStaff.add(totalStaffCounts, totalStaff);


        // Card Six
        VerticalLayout cardForFinancialOverview = new VerticalLayout();
        cardForFinancialOverview.addClassNames("financial-overview", "total-clients");
        cardForFinancialOverview.getStyle().set("text-align", "start").set("align-items", "start").set("justify-content", "center");

        HorizontalLayout financialOverviewText = new HorizontalLayout(new Text("Financial Overview"));
        cardForFinancialOverview.add(financialOverviewText);

        // Card Seven
        VerticalLayout cardForRecentCustomers = new VerticalLayout();
        cardForRecentCustomers.addClassNames("card-for-customers", "total-clients");
        cardForRecentCustomers.getStyle().set("text-align", "start").set("align-items", "start").set("justify-content", "center").set("margin-left", "10px");

        HorizontalLayout recentCustomerText = new HorizontalLayout(new Text("Recent Customers"));

        List<Users> recentCustomers = userService.findUserByUserRoleClient();
        VerticalLayout recentClientsLayout = new VerticalLayout();

        for (Users user : recentCustomers) {
            String userFirstName = user.getFirstName();
            String userLastName = user.getLastName();
            String name = userLastName + " " + userFirstName;

            HtmlComponent nameComponent = new HtmlComponent("div");
            nameComponent.getElement().setText(name);
            recentClientsLayout.add(nameComponent);
        }

        Button linkToUsersPage = new Button("View All");

        linkToUsersPage.addClickListener(clickEvent ->
                UI.getCurrent().navigate("userView"));

        linkToUsersPage.addClassName("view-all");


        cardForRecentCustomers.add(recentCustomerText, recentClientsLayout, linkToUsersPage);

        topLayout.add(cardForAllProperties, cardForAllLandedProperties, cardForResidentialProperties, cardForClients, cardForStaff);
        topLayoutTwo.add(cardForFinancialOverview, cardForRecentCustomers);

        topLayout.setWidthFull();
        topLayoutTwo.setWidthFull();
        mainLayout.setWidthFull();

        topLayout.addClassName("overall-layout");
        topLayoutTwo.addClassName("overall-layout");
        mainLayout.addClassName("overall-layout");

        getStyle().set("background-color", "#F7F5F5");
        mainLayout.add(topLayout, topLayoutTwo);
        add(mainLayout);
    }
}

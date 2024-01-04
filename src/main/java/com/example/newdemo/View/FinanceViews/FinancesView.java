package com.example.newdemo.View.FinanceViews;


import com.example.newdemo.View.MainView;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;

@Route(value = "financesView", layout = MainView.class)
public class FinancesView extends VerticalLayout implements RouterLayout {
}

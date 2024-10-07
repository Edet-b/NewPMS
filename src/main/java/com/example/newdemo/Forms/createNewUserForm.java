package com.example.newdemo.Forms;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class createNewUserForm extends VerticalLayout {

    Text exist = new Text("User doesn't exist");
    public Button create = new Button("Create User");
    public Button cancel = new Button("Cancel");

    public createNewUserForm(){

        FormLayout createUserFormLayout = new FormLayout(exist);
        createUserFormLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));
        createUserFormLayout.setSizeFull();
        createUserFormLayout.getStyle().set("width", "fit-content");

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.add(createUserFormLayout);

        add(mainLayout);
        }

        public HorizontalLayout createNewUserButtonLayout(){
            create.addClickShortcut(Key.ENTER);
            cancel.addClickShortcut(Key.CANCEL);

            return new HorizontalLayout(cancel, create);
        }
}

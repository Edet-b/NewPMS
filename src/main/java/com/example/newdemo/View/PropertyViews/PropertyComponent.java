package com.example.newdemo.View.PropertyViews;

import com.example.newdemo.Entity.Property;
import com.example.newdemo.Entity.PropertyImage;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.util.List;

public class PropertyComponent extends Div {
    public PropertyComponent(Property property) {
        List<PropertyImage> imageList = property.getPropertyImages();

        if (imageList != null && !imageList.isEmpty()) {
            byte[] imageData = imageList.get(0).getPropertyImages();

            if (imageData != null && imageData.length > 0) {
                StreamResource resource = new StreamResource("image.png", () -> new ByteArrayInputStream(imageData));
                Image image = new Image(resource, "Image");
                image.getStyle().set("height", "100px").set("width", "100px");
                add(image);
                return;
            }
        }
        add(new Text("No image found"));
    }
}

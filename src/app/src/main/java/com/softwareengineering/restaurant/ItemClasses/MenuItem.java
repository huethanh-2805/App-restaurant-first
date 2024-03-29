package com.softwareengineering.restaurant.ItemClasses;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.Locale;

public class MenuItem {
    private String imageURL;
    private String name;
    private long price;
    private String type;
    private Long quantity;

    public MenuItem(String imageURL, String name, long price, String type, Long quantity) {
        this.imageURL = imageURL;
        this.name = name;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public StorageReference getImageReference() {
        if (imageURL == null){
            return null;
        }
        return FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    // Phương thức để định dạng giá thành chuỗi dạng "20.000"
    public String getFormattedPrice() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(price);
    }

}

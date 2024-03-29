package com.softwareengineering.restaurant.ItemClasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderItem implements Parcelable {
    private String nameFood;
    private Long price;
    private Integer quantity;

    public OrderItem(String nameFood, Long price, Integer quantity) {
        this.nameFood = nameFood;
        this.price = price;
        this.quantity = quantity;
    }

    public String getNameFood() {
        return nameFood;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    protected OrderItem(Parcel in) {
        nameFood = in.readString();
        price = in.readLong();
        quantity = in.readInt();
    }

    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameFood);
        dest.writeLong(price);
        dest.writeInt(quantity);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "nameFood='" + nameFood + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}

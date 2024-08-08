package com.uncc.inclass12;

import android.widget.TextView;

import androidx.annotation.NonNull;

public class Contact {
    private String name, email, phoneNumber, contactImage, documentId;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public Contact(String name, String email, String phoneNumber, String contactImage, String documentId) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.contactImage = contactImage;
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactImage() {
        return contactImage;
    }

    public  String getDocumentId() {return documentId;}
}

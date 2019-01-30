package com.digitalhouse.instapartyapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Photos implements Parcelable {

    private String urlPhoto;
    private String tituloPhoto;

    public Photos(String urlPhoto, String tituloPhoto) {
        this.urlPhoto = urlPhoto;
        this.tituloPhoto = tituloPhoto;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getTituloPhoto() {
        return tituloPhoto;
    }

    public void setTituloPhoto(String tituloPhoto) {
        this.tituloPhoto = tituloPhoto;
    }

    protected Photos(Parcel in) {
    }

    public static final Creator<Photos> CREATOR = new Creator<Photos>() {
        @Override
        public Photos createFromParcel(Parcel in) {
            return new Photos(in);
        }

        @Override
        public Photos[] newArray(int size) {
            return new Photos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}


package com.example.kingstreinos.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;



public class Exercise extends ExerciseSet implements Parcelable {
    private String NAME;
    private String DESCRIPTION;
    private Uri IMAGE;
    private int ID;

    private static final long serialVersionUID = 2L;

    public Exercise(int id, String name, String description, Uri image) {
        this.ID = id;
        this.NAME = name;
        this.DESCRIPTION = description;
        this.IMAGE = image;
    }

    protected Exercise(Parcel in) {
        NAME = in.readString();
        DESCRIPTION = in.readString();
        IMAGE = in.readParcelable(Uri.class.getClassLoader());
        ID = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return NAME;
    }

    public void setName(String name) { this.NAME = name; }

    public String getDescription() {
        return DESCRIPTION;
    }

    public void setDescription(String description) {
        this.DESCRIPTION = description;
    }

    public Uri getImage(){ return IMAGE; }

    public void setImage(Uri image) { this.IMAGE = image; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(NAME);
        parcel.writeString(DESCRIPTION);
        parcel.writeParcelable(IMAGE, i);
        parcel.writeInt(ID);
    }
}

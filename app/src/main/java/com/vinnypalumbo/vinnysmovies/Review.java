package com.vinnypalumbo.vinnysmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vincent on 2015-11-19.
 */
public class Review implements Parcelable {
    String author;
    String content;

    public Review(String author, String content)
    {
        this.author = author;
        this.content = content;
    }

    private Review(Parcel in){
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return author + "--" + content ; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }

    public final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }

    };
}

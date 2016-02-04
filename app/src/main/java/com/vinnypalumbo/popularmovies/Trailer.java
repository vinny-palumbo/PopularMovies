package com.vinnypalumbo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vincent on 2016-02-04.
 */
public class Trailer implements Parcelable {
    String name;
    String key;

    public Trailer(String name, String key)
    {
        this.name = name;
        this.key = key;
    }

    private Trailer(Parcel in){
        name = in.readString();
        key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return name + "--" + key ; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(key);
    }

    public final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        @Override
        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }

    };
}

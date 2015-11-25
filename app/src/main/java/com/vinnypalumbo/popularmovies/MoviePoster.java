package com.vinnypalumbo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vincent on 2015-11-19.
 */
public class MoviePoster implements Parcelable {

    String originalTitle;
    String posterPath;
    String overview;
    Double voteAverage;
    String releaseDate;


    public MoviePoster(String title, String poster, String plot, Double rating, String date)
    {
        this.originalTitle = title;
        this.posterPath = poster;
        this.overview = plot;
        this.voteAverage = rating;
        this.releaseDate = date;
    }

    private MoviePoster(Parcel in){
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return originalTitle + " -- " + posterPath + " -- " + overview + " -- " + voteAverage + " -- " + releaseDate; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeDouble(voteAverage);
        parcel.writeString(releaseDate);
    }

    public final Parcelable.Creator<MoviePoster> CREATOR = new Parcelable.Creator<MoviePoster>() {
        @Override
        public MoviePoster createFromParcel(Parcel parcel) {
            return new MoviePoster(parcel);
        }

        @Override
        public MoviePoster[] newArray(int i) {
            return new MoviePoster[i];
        }

    };
}

package com.vinnypalumbo.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vincent on 2015-11-19.
 */
public class Movie implements Parcelable {
    int movieId;
    String originalTitle;
    String posterPath;
    String overview;
    String voteAverage;
    String releaseDate;


    public Movie(int movieId, String title, String poster, String plot, String rating, String date)
    {
        this.movieId = movieId;
        this.originalTitle = title;
        this.posterPath = poster;
        this.overview = plot;
        this.voteAverage = rating;
        this.releaseDate = date;
    }

    private Movie(Parcel in){
        movieId = in.readInt();
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return String.valueOf(movieId) + "--" + originalTitle + " -- " + posterPath + " -- " + overview + " -- " + voteAverage + " -- " + releaseDate; }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(movieId);
        parcel.writeString(originalTitle);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(voteAverage);
        parcel.writeString(releaseDate);
    }

    public final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}

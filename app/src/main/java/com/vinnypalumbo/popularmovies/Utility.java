package com.vinnypalumbo.popularmovies;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utility {

    public static String getSortSetting(Context context) {
        Log.d("vinny-debug", "Utility - getPreferredSorting");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    static String formatMovieId(int movieId){
        Log.d("vinny-debug", "Utility - formatMovieId");
        return String.valueOf(movieId);
    }

    static String formatVoteAverage(Context context, double voteAverage){
        Log.d("vinny-debug", "Utility - formatVoteAverage");
        return context.getString(R.string.format_rating, voteAverage);
    }

    static String formatReleaseDate(int releaseDate){
        Log.d("vinny-debug", "Utility - formatReleaseDate");
        return String.valueOf(releaseDate);
    }

    // Function taken from http://www.rgagnon.com/javadetails/java-0506.html
    // I'm no rocket scientist...
    public static String julianDateToAPIFormatConversion(int injulian) {
        int JGREG= 15 + 31*(10+12*1582);
        double HALFSECOND = 0.5;

        int jalpha,ja,jb,jc,jd,je,year,month,day;
        double julian = injulian  + HALFSECOND / 86400.0;
        ja = (int) julian;
        if (ja>= JGREG) {

            jalpha = (int) (((ja - 1867216) - 0.25) / 36524.25);
            ja = ja + 1 + jalpha - jalpha / 4;
        }
        jb = ja + 1524;
        jc = (int) (6680.0 + ((jb - 2439870) - 122.1) / 365.25);
        jd = 365 * jc + jc / 4;
        je = (int) ((jb - jd) / 30.6001);
        day = jb - jd - (int) (30.6001 * je);
        month = je - 1;
        if (month > 12) month = month - 12;
        year = jc - 4715;
        if (month > 2) year--;
        if (year <= 0) year--;

        return String.valueOf(year) + "-" + String.valueOf(month) + "-" +  String.valueOf(day);
    }
}
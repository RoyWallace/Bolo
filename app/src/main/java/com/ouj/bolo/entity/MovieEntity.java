package com.ouj.bolo.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/7.
 */
public class MovieEntity implements Serializable {
    public int count;//
    public int start;//
    public int total;//
    public ArrayList<Subject> subjects;
    public String title;

    public static class Subject implements Serializable{
        public Rating rating;
        public String[] genres;
        public String title;
        public ArrayList<Cast> casts;
        public long collect_count;
        public String original_title;
        public String subtype;
        public ArrayList<Cast> directors;
        public String year;
        public Avatars images;
        public String alt;
        public String id;
    }

    public static class Rating implements Serializable{
        public int max;
        public float average;
        public String stars;
        public int min;
    }

    public static class Cast implements Serializable{
        public String alt;
        public Avatars avatars;
        public String name;
        public long id;
    }

    public static class Avatars implements Serializable{
        public String small;
        public String large;
        public String medium;
    }

}

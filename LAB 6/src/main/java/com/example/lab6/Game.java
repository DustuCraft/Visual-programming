package com.example.lab6;

public class Game {
    private String name;
    private String genre;
    private String platform;
    private double rating;

    public Game(String name, String genre, String platform, double rating) {
        this.name = name;
        this.genre = genre;
        this.platform = platform;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getGenre() { return genre; }
    public String getPlatform() { return platform; }
    public double getRating() { return rating; }
}
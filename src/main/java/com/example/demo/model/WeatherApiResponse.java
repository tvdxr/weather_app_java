package com.example.demo.model;

public class WeatherApiResponse {
    private String name;
    private MainData main;
    private WeatherDescription[] weather;
    private WindData wind;

    public String getName() { return name; }
    public MainData getMain() { return main; }
    public WeatherDescription[] getWeather() { return weather; }
    public WindData getWind() { return wind; }

}

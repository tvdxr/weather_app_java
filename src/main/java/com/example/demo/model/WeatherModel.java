package com.example.demo.model;

public class WeatherModel {
    String city;
    String temperature;
    String description;
    String humidity;
    String windSpeed;
    String feelsLike;

    public WeatherModel(String city, String temperature, String description, String humidity, String windSpeed, String feelsLike) {
        this.city = city;
        this.temperature = temperature;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.feelsLike = feelsLike;
    }

    public String getCity() {
        return city;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getFeelsLike() {
        return feelsLike;
    }    
}

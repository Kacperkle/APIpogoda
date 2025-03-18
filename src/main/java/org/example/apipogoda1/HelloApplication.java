package org.example.apipogoda1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class HelloApplication extends Application {

    private static final String API_KEY = "ab4d32a7264bccfb28b8fc1b11a9bc62";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=pl&units=metric";

    private ImageView weatherIcon;
    private TextArea weatherInfo;

    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("Pogoda");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        weatherIcon = new ImageView();
        weatherIcon.setFitWidth(100);
        weatherIcon.setFitHeight(100);

        TextField cityInput = new TextField();
        cityInput.setPromptText("Wpisz miasto");

        cityInput.setPrefWidth(320);

        Button searchButton = new Button("🔍");
        searchButton.setOnAction(e -> fetchWeather(cityInput.getText()));

        HBox city = new HBox(10, cityInput, searchButton);


        weatherInfo = new TextArea();
        weatherInfo.setEditable(false);
        weatherInfo.setPrefHeight(150);
        weatherInfo.setWrapText(true);

        Button closeButton = new Button("Zamknij");
        closeButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(15, title, weatherIcon, city, weatherInfo, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px; -fx-background-color: #ADD8E6;");

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setTitle("Pogoda");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchWeather(String city) {
        if (city.isEmpty()) {
            weatherInfo.setText("Wpisz nazwę miasta!");
            return;
        }

        try {
            String encodedCity = URLEncoder.encode(city, "UTF-8");

            String urlString = String.format(BASE_URL, encodedCity, API_KEY);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream responseStream = conn.getInputStream();
            Scanner scanner = new Scanner(responseStream);
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            parseWeatherData(response);

        } catch (Exception e) {
            weatherInfo.setText("Błąd: Nie znaleziono miasta.");
            weatherIcon.setImage(null);
        }
    }


    private void parseWeatherData(String json) {
        try {
            String cityName = extractValue(json, "\"name\":\"", "\"");
            String description = extractValue(json, "\"description\":\"", "\"");
            String temp = extractValue(json, "\"temp\":", ",");
            String humidity = extractValue(json, "\"humidity\":", ",");
            String windSpeed = extractValue(json, "\"speed\":", ",");
            String clouds = extractValue(json, "\"all\":", ",");
            String iconCode = extractValue(json, "\"icon\":\"", "\"");

            clouds = clouds.substring(0, clouds.length() - 1);

            String weatherText = String.format("""
                    Miasto: %s
                    Temperatura: %s°C
                    Wilgotność: %s%%
                    Wiatr: %s m/s
                    Zachmurzenie: %s%%
                    Opis: %s
                    """, cityName, temp, humidity, windSpeed, clouds, description);

            weatherInfo.setText(weatherText);

            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
            weatherIcon.setImage(new Image(iconUrl));

        } catch (Exception e) {
            weatherInfo.setText("Błąd przetwarzania danych.");
            weatherIcon.setImage(null);
        }
    }

    private String extractValue(String json, String key, String endChar) {
        int start = json.indexOf(key);
        if (start == -1) return "Brak danych";
        start += key.length();
        int end = json.indexOf(endChar, start);
        return end == -1 ? json.substring(start) : json.substring(start, end);
    }

    public static void main(String[] args) {
        launch();
    }
}

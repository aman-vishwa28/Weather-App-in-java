import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * CODETECH SOLUTION INTERNSHIP TASK 2
 * 
 * WEATHER APP
 * A JAVA PROGRAM THAT HANDLES HTTP REQUESTS AND PARSES JSON RESPONSES.
 * 
 * Features:
 * 1. Can Fetech Weather for any City 
 * 2. Basic code format for java implementation using Rest Api 
 */

public class WeatherDataFetcher {
    private static final String API_KEY = ""; // API key removed for security
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String cityName;
        
        System.out.println("Weather Data Fetcher");
        System.out.println("Enter a city name to get weather data or type 'close' to exit");
        
        while (true) {
            System.out.print("\nEnter city name: ");
            cityName = scanner.nextLine().trim();
            
            if (cityName.equalsIgnoreCase("close")) {
                System.out.println("Exiting Weather Data Fetcher. Goodbye!");
                break;
            }
            
            if (cityName.isEmpty()) {
                System.out.println("Please enter a valid city name.");
                continue;
            }
            
            fetchAndDisplayWeatherData(cityName);
        }
        
        scanner.close();
    }
    
    private static void fetchAndDisplayWeatherData(String cityName) {
        try {
            // Build the request URL
            String requestUrl = String.format("%s?q=%s&appid=%s&units=metric", 
                BASE_URL, cityName.replace(" ", "%20"), API_KEY);
            
            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();
            
            // Create HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .build();
            
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            
            // Check if the request was successful
            if (response.statusCode() == 200) {
                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.body());
                
                // Extract and display the weather data
                displayWeatherData(jsonResponse);
            } else {
                JSONObject errorResponse = new JSONObject(response.body());
                System.out.println("Error fetching weather data for " + cityName + ": " + 
                    errorResponse.optString("message", "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred while fetching data for " + cityName + ": " + 
                e.getMessage());
        }
    }
    
    private static void displayWeatherData(JSONObject weatherData) {
        try {
            // Extract main data
            JSONObject main = weatherData.getJSONObject("main");
            JSONObject wind = weatherData.getJSONObject("wind");
            JSONObject sys = weatherData.getJSONObject("sys");
            
            // Extract weather array (first element)
            JSONObject weather = weatherData.getJSONArray("weather").getJSONObject(0);
            
            // Display the structured weather information
            System.out.println("\n=== WEATHER INFORMATION ===");
            System.out.println("Location: " + weatherData.getString("name") + ", " + sys.getString("country"));
            System.out.println("Weather: " + weather.getString("main") + " (" + weather.getString("description") + ")");
            System.out.println("Temperature: " + main.getDouble("temp") + "°C");
            System.out.println("Feels Like: " + main.getDouble("feels_like") + "°C");
            System.out.println("Min/Max Temp: " + main.getDouble("temp_min") + "°C / " + main.getDouble("temp_max") + "°C");
            System.out.println("Humidity: " + main.getInt("humidity") + "%");
            System.out.println("Pressure: " + main.getInt("pressure") + " hPa");
            
            // Wind information might not always have 'deg'
            String windInfo = wind.getDouble("speed") + " m/s";
            if (wind.has("deg")) {
                windInfo += ", " + wind.getInt("deg") + "°";
            }
            System.out.println("Wind: " + windInfo);
            
            System.out.println("Cloudiness: " + weatherData.getJSONObject("clouds").getInt("all") + "%");
            
            // Visibility is optional
            if (weatherData.has("visibility")) {
                System.out.println("Visibility: " + weatherData.getInt("visibility") + " meters");
            }
            
            System.out.println("Sunrise: " + formatUnixTime(sys.getLong("sunrise")));
            System.out.println("Sunset: " + formatUnixTime(sys.getLong("sunset")));
            System.out.println("===========================\n");
        } catch (Exception e) {
            System.out.println("Error parsing weather data: " + e.getMessage());
        }
    }
    
    private static String formatUnixTime(long unixTime) {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(unixTime * 1000));
    }
}
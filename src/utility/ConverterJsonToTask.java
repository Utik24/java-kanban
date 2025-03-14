package utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import deserializers.DurationDeserializer;
import deserializers.LocalDateTimeDeserializer;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

public class ConverterJsonToTask {
    public static Task converterTaskFromJson(HttpExchange exchange) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        Gson customGson = gsonBuilder.create();
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        System.out.println("Received JSON: " + requestBody);

        Map jsonMap = customGson.fromJson(requestBody.toString(), Map.class);

        String title = (String) jsonMap.get("title");
        String description = (String) jsonMap.get("description");
        String durationString = (String) jsonMap.get("duration");
        String startTimeString = (String) jsonMap.get("startTime");

        Duration duration = Duration.parse(durationString);
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);

        Task task = new Task(title, description, duration, startTime);

        return task;
    }

    public static SubTask converterSubTaskFromJson(HttpExchange exchange) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        Gson customGson = gsonBuilder.create();
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        System.out.println("Received JSON: " + requestBody);

        Map jsonMap = customGson.fromJson(requestBody.toString(), Map.class);

        String title = (String) jsonMap.get("title");
        String description = (String) jsonMap.get("description");
        String durationString = (String) jsonMap.get("duration");
        String startTimeString = (String) jsonMap.get("startTime");

        Duration duration = Duration.parse(durationString);
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);

        SubTask subTask = new SubTask(title, description, duration, startTime);

        return subTask;
    }

    public static Epic converterEpicFromJson(HttpExchange exchange) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        Gson customGson = gsonBuilder.create();
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        System.out.println("Received JSON: " + requestBody);

        Map jsonMap = customGson.fromJson(requestBody.toString(), Map.class);

        String title = (String) jsonMap.get("title");
        String description = (String) jsonMap.get("description");

        Epic epic = new Epic(title, description);

        return epic;
    }
}

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import deserializers.DurationDeserializer;
import deserializers.LocalDateTimeDeserializer;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import model.Epic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicHandlerTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/epics";
    private static HttpTaskServer server;
    private Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationDeserializer()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).setPrettyPrinting().create();

    @BeforeAll
    static void startServer() throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.initServer();
    }

    @AfterAll
    static void stopServer() {
        server.closeServer();
    }

    @Test
    void testCreateEpicSuccess() throws IOException, InterruptedException {
        // JSON-объект эпика
        String jsonEpic = """
                {
                  "title": "Epic 1",
                  "description": "Description 1"
                }
                """;

        // Отправляем POST-запрос
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).POST(HttpRequest.BodyPublishers.ofString(jsonEpic)).header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 201
        assertEquals(201, response.statusCode());
    }

    @Test
    void testGetAllEpicsSuccess() throws IOException, InterruptedException {
        // Отправляем GET-запрос
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 200
        assertEquals(200, response.statusCode());

        // Десериализуем список эпиков
        Type epicListType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(response.body(), epicListType);

        // Проверяем, что список не пустой
        assertNotNull(epics);
    }

    @Test
    void testDeleteEpicSuccess() throws IOException, InterruptedException {
        String jsonEpic = """
                {
                  "title": "Epic 1",
                  "description": "Description 1"
                }
                """;

        // Отправляем POST-запрос
        HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).POST(HttpRequest.BodyPublishers.ofString(jsonEpic)).header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 201
        assertEquals(201, response.statusCode());
        // Отправляем DELETE-запрос
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/1")) // Удаляем эпик с id=1
                .DELETE().build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 200
        assertEquals(200, response2.statusCode());
    }

    @Test
    void testDeleteNonExistentEpic() throws IOException, InterruptedException {
        // Отправляем DELETE-запрос к несуществующему id
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/999")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Ожидаем 404 Not Found
        assertEquals(404, response.statusCode());
    }
}

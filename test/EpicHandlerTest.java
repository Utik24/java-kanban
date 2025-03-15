import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import deserializers.DurationDeserializer;
import deserializers.LocalDateTimeDeserializer;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import model.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

class EpicHandlerTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/epics";
    private static HttpTaskServer server;
    private Gson gson = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationDeserializer()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer()).setPrettyPrinting().create();

    @BeforeEach
    void startServer() throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.initServer();
    }

    @AfterEach
    void stopServer() {
        server.closeServer();
    }

    @Test
    void testCreateEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 201
        assertEquals(201, response.statusCode());
        assertEquals(1, server.getTaskManager().getAllEpics().size());
    }

    @Test
    void testGetAllEpicsSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).header("Content-Type", "application/json").build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        // Отправляем GET-запрос
        request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 200
        assertEquals(200, response.statusCode());

        // Десериализуем список эпиков
        Type epicListType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(response.body(), epicListType);

        // Проверяем, что список не пустой
        assertEquals(server.getTaskManager().getAllEpics(), epics);
    }

    @Test
    void testDeleteEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Epic Description");

        HttpRequest request1 = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic))).header("Content-Type", "application/json").build();
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
        int mapSize = server.getTaskManager().getAllEpics().size();
        // Отправляем DELETE-запрос к несуществующему id
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/999")).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Ожидаем 404 Not Found
        assertEquals(mapSize, server.getTaskManager().getAllEpics().size());
    }
}

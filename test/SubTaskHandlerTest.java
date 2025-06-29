import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import deserializers.DurationDeserializer;
import deserializers.LocalDateTimeDeserializer;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import model.SubTask;
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

class SubTaskHandlerTest {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080/subtasks";
    private static HttpTaskServer server;
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .setPrettyPrinting()
            .create();

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
    void testCreateSubTaskSuccess() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 201
        assertEquals(201, response.statusCode());
        assertEquals(subTask.getTitle(), server.getTaskManager().getSubTaskById(1).getTitle());
    }

    @Test
    void testGetAllSubTasksSuccess() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        server.getTaskManager().createSubtask(subTask);
        // Отправляем GET-запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 200
        assertEquals(200, response.statusCode());

        // Десериализуем список подзадач
        Type subTaskListType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subTasks = gson.fromJson(response.body(), subTaskListType);

        // Проверяем, что список не пустой
        assertEquals(server.getTaskManager().getAllSubtasks(), subTasks);
    }

    @Test
    void testDeleteSubTaskSuccess() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("Subtask 1", "Subtask Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 201
        assertEquals(201, response.statusCode());
        // Отправляем DELETE-запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/1")) // Удаляем подзадачу с id=1
                .DELETE()
                .build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус 200
        assertEquals(204, response2.statusCode());
    }

    @Test
    void testDeleteNonExistentSubTask() throws IOException, InterruptedException {
        // Отправляем DELETE-запрос к несуществующему id
        int mapSize = server.getTaskManager().getAllSubtasks().size();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/999"))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Ожидаем 404 Not Found
        assertEquals(mapSize, server.getTaskManager().getAllSubtasks().size());
    }
}

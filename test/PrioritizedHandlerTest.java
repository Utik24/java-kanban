import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializers.DurationDeserializer;
import deserializers.LocalDateTimeDeserializer;
import managers.InMemoryTaskManager;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedHandlerTest {
    private InMemoryTaskManager manager = new InMemoryTaskManager();
    private HttpTaskServer taskServer = new HttpTaskServer(manager);
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationDeserializer())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
            .setPrettyPrinting()
            .create();

    @BeforeEach
    public void setUp() throws IOException {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpics();
        taskServer.initServer();
    }

    @AfterEach
    public void shutDown() {
        taskServer.closeServer();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        String jsonTask = """
                {
                  "title": "Task 1",
                  "description": "Description 1",
                  "duration": "PT15M",
                  "startTime": "2022-01-01T00:00:00"
                }
                """;
        Task task = gson.fromJson(jsonTask, Task.class);
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении приоритетных задач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task 1"), "Ответ не содержит имя приоритетной задачи");
    }
}

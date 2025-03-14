import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    protected Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
    protected Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(15), LocalDateTime.of(2023, 1, 1, 0, 0));
    protected Epic epic = new Epic("Epic 1", "Epic Description");
    protected SubTask subTask = new SubTask("Subtask 1", "Subtask Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
    protected SubTask subTask2 = new SubTask("Subtask 2", "Subtask Description 2", Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 0, 0));
    private InMemoryTaskManager manager = new InMemoryTaskManager();
    private HttpTaskServer taskServer = new HttpTaskServer(manager);
    private Gson gson = new Gson();

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
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = new String(Files.readAllBytes(Paths.get("C:\\Users\\кирилл\\IdeaProjects\\java-kanban\\test\\jsons\\testTask.json")));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус ответа при добавлении задачи");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении задач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task 1"), "Ответ не содержит имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при удалении задачи");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertTrue(tasksFromManager.isEmpty(), "Задача не была удалена");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении приоритетных задач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task 1"), "Ответ не содержит имя приоритетной задачи");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        manager.createTask(task);
        manager.getTaskById(1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении истории");
        String responseBody = response.body();
        assertEquals("[Task{title='Task 1', description='Description 1', id=1, status=NEW, duration=PT15M, startTime=2021-01-01T00:00}]", responseBody, "Ответ не содержит имя задачи в истории");
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        String SubTaskJson = new String(Files.readAllBytes(Paths.get("C:\\Users\\кирилл\\IdeaProjects\\java-kanban\\test\\jsons\\testTask.json")));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(SubTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус ответа при добавлении задачи");

        List<SubTask> tasksFromManager = manager.getAllSubtasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task 1", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubTask() throws IOException, InterruptedException {
        manager.createSubtask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении подзадач");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Subtask 1"), "Ответ не содержит имя подзадачи");
    }

    @Test
    public void testDeleteSubTask() throws IOException, InterruptedException {
        manager.createSubtask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при удалении подзадачи");

        List<SubTask> subTasksFromManager = manager.getAllSubtasks();
        assertTrue(subTasksFromManager.isEmpty(), "Подзадача не была удалена");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String epicJson = new String(Files.readAllBytes(Paths.get("C:\\Users\\кирилл\\IdeaProjects\\java-kanban\\test\\jsons\\testEpic.json")));


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус ответа при добавлении эпика");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic 1", epicsFromManager.get(0).getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при получении эпиков");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Epic 1"), "Ответ не содержит имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус ответа при удалении эпика");

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertTrue(epicsFromManager.isEmpty(), "Эпик не был удалён");
    }
}

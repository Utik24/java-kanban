import managers.FileBackedTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;
    private Task task;


    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
        task = new Task("Task 1", "Description 1");

    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        assertTrue(manager.getAllTasks().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadAllTaskTypesCorrectly() throws IOException {
        manager.createTask(task);
        int taskId = task.getId();
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        int epicId = epic.getId();

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epicId);
        manager.createSubtask(subtask);
        int subtaskId = subtask.getId();

        System.out.println("Содержимое файла перед загрузкой:");
        Files.readAllLines(tempFile.toPath()).forEach(System.out::println);
        FileBackedTaskManager loadedManager = new FileBackedTaskManager(tempFile);
        loadedManager.loadFromFile();

        assertEquals(1, loadedManager.getAllTasks().size(), "Количество задач не совпадает");
        assertEquals(1, loadedManager.getAllEpics().size(), "Количество эпиков не совпадает");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Количество подзадач не совпадает");

        assertEquals(task, loadedManager.getTaskById(taskId), "Задача восстановлена некорректно");
        assertEquals(epic, loadedManager.getEpicById(epicId), "Эпик восстановлен некорректно");
        assertEquals(subtask, loadedManager.getSubTaskById(subtaskId), "Подзадача восстановлена некорректно");
    }

    @Test
    void shouldLoadFromEmptyFileWithoutErrors() throws IOException {
        Files.write(tempFile.toPath(), List.of());

        assertTrue(manager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
        assertTrue(manager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void shouldThrowExceptionWhenLoadingCorruptedFile() throws IOException {
        Files.write(tempFile.toPath(), List.of("id,type,name,status,description,epic", "abc,INVALID,broken,NEW,test,123"));

        assertThrows(IllegalArgumentException.class, () -> new FileBackedTaskManager(tempFile).loadFromFile());
    }
}

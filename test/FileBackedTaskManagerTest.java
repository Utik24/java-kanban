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
        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);
        int epicId = epic.getId();
        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epicId);
        manager.createSubtask(subtask);

        System.out.println("Содержимое файла перед загрузкой:");
        Files.readAllLines(tempFile.toPath()).forEach(System.out::println);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(manager.getAllSubtasks(), loadedManager.getAllSubtasks(), "Списки подзадач не совпадают");
        assertEquals(manager.getAllTasks(), loadedManager.getAllTasks(), "Списки задач не совпадают");
        assertEquals(manager.getAllEpics(), loadedManager.getAllEpics(), "Списки эпиков не совпадают");

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

        assertThrows(IllegalArgumentException.class, () -> FileBackedTaskManager.loadFromFile(tempFile));
    }
}

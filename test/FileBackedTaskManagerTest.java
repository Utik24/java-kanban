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
        task = new Task("Test Task", "Description");
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        manager.createTask(task);
        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.createEpic(epic);
        SubTask subtask = new SubTask("Test SubTask", "SubTask Description", epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasks();
        List<Epic> epics = loadedManager.getAllEpics();
        List<SubTask> subtasks = loadedManager.getAllSubtasks();

        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());
    }

    @Test
    void shouldRemoveTasksAndSaveChanges() {
        manager.createTask(task);
        manager.removeTaskById(task.getId());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty());
    }
}

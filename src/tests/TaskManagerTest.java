package tests;

import managers.InMemoryTaskManager;
import managers.Managers;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 1", "Description 1");
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    void testSubTaskEqualityById() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", 1);
        subTask1.setId(1);
        SubTask subTask2 = new SubTask("SubTask 1", "Description 1", 1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2);
    }

    @Test
    void testEpicCannotAddItselfAsSubTask() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        epic.setId(1); // Устанавливаем ID для эпика

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        subtask.setId(2); // Устанавливаем ID для подзадачи

        // Проверяем, что при добавлении подзадачи, которая является её собственным эпиком, выбрасывается исключение
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(subtask);
        });
        assertEquals("Subtask can't be Epic", thrown.getMessage());
    }

    @Test
    void testSubTaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        epic.setId(1); // Устанавливаем ID для эпика

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        subtask.setId(2); // Устанавливаем ID для подзадачи

        // Проверяем, что при добавлении подзадачи, которая является её собственным эпиком, выбрасывается исключение
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(subtask); // Пытаемся добавить подзадачу как свой эпик
        });

        assertEquals("Subtask can't be Epic", thrown.getMessage());
    }

    @Test
    void testManagersReturnInitializedInstances() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }

    @Test
    void testTaskManagerAddsTasksAndFindsById() {
        Task task = new Task("Task 1", "Description 1");
        task.setId(taskManager.generateId());
        taskManager.createTask(task);

        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @Test
    void testIdConflictInTaskManager() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(1);
        taskManager.createTask(task2);

        assertEquals(task1, taskManager.getTaskById(1));
        assertNotEquals(task2, taskManager.getTaskById(1));
    }

    @Test
    void testTaskImmutabilityOnAddition() {
        Task task = new Task("Task 1", "Description 1");
        task.setId(taskManager.generateId());
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getTitle(), retrievedTask.getTitle());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }

    @Test
    void testHistoryManagerRetainsPreviousVersions() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(taskManager.generateId());
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(taskManager.generateId());
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getId());


        List<Task> history = taskManager.getHistory();


        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertEquals(2, history.size());
    }

}

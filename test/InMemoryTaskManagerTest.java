import exceptions.IntersectionException;
import managers.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private InMemoryTaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subtask;
    private SubTask subtask2;

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        task2 = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
        epic1 = new Epic("Epic 1", "Description 1");
        epic2 = new Epic("Epic 2", "Description 2");
        epic1.setId(100);
        subtask = new SubTask("Subtask 1", "Subtask Description", epic1.getId(), Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
        subtask2 = new SubTask("Subtask 1", "Subtask Description", epic1.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
    }

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    //вот тест проверяющий правильна ли работает сортировка, он был
    @Test
    void testGetPrioritizedTasks() throws IntersectionException {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(15), LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task3 = new Task("Task 3", "Description 3", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println(taskManager.getPrioritizedTasks());
        assertEquals(taskManager.getPrioritizedTasks(), List.of(task3, task1, task2));
    }


    @Test
    void testIdConflictInTaskManager() throws IntersectionException {
        task1.setId(1);
        taskManager.createTask(task1);
        task2.setId(1);
        taskManager.createTask(task2);
        assertEquals(task1, taskManager.getTaskById(1));
        assertNotEquals(task2, taskManager.getTaskById(1));
    }

    @Test
    void testTaskImmutabilityOnAddition() throws IntersectionException {
        taskManager.createTask(task1);
        Task retrievedTask = taskManager.getTaskById(task1.getId());
        assertEquals(task1.getTitle(), retrievedTask.getTitle());
        assertEquals(task1.getDescription(), retrievedTask.getDescription());
        assertEquals(task1.getStatus(), retrievedTask.getStatus());
    }

}

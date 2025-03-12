import managers.InMemoryTaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testGetPrioritizedTasks() {
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
    void testTaskManagerAddsTasksAndFindsById() {
        taskManager.createTask(task1);
        assertEquals(task1, taskManager.getTaskById(task1.getId()));
    }

    @Test
    void testIdConflictInTaskManager() {
        task1.setId(1);
        taskManager.createTask(task1);
        task2.setId(1);
        taskManager.createTask(task2);
        assertEquals(task1, taskManager.getTaskById(1));
        assertNotEquals(task2, taskManager.getTaskById(1));
    }

    @Test
    void testTaskImmutabilityOnAddition() {
        taskManager.createTask(task1);
        Task retrievedTask = taskManager.getTaskById(task1.getId());
        assertEquals(task1.getTitle(), retrievedTask.getTitle());
        assertEquals(task1.getDescription(), retrievedTask.getDescription());
        assertEquals(task1.getStatus(), retrievedTask.getStatus());
    }

    @Test
    void testCreateTask() {
        taskManager.createTask(task1);
        assertNotNull(taskManager.getTaskById(task1.getId()), "Task should be created and retrievable");
        assertEquals("Task 1", taskManager.getTaskById(task1.getId()).getTitle(), "Task title should match");
    }

    @Test
    void testCreateEpic() {
        taskManager.createEpic(epic1);
        assertNotNull(taskManager.getEpicById(epic1.getId()), "Epic should be created and retrievable");
        assertEquals("Epic 1", taskManager.getEpicById(epic1.getId()).getTitle(), "Epic title should match");
    }

    @Test
    void testCreateSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask);
        assertNotNull(taskManager.getSubTaskById(subtask.getId()), "Subtask should be created and retrievable");
        assertEquals("Subtask 1", taskManager.getSubTaskById(subtask.getId()).getTitle(), "Subtask title should match");
    }

    @Test
    void testRemoveTaskById() {
        taskManager.createTask(task1);
        taskManager.removeTaskById(task1.getId());
        assertNull(taskManager.getTaskById(task1.getId()), "Task should be removed");
    }

    @Test
    void testRemoveEpicById() {
        taskManager.createEpic(epic1);
        taskManager.removeEpicById(epic1.getId());
        assertNull(taskManager.getEpicById(epic1.getId()), "Epic should be removed");
    }

    @Test
    void testRemoveSubtaskById() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask);
        taskManager.removeSubTaskById(subtask.getId());
        assertNull(taskManager.getSubTaskById(subtask.getId()), "Subtask should be removed");
    }

    @Test
    void testUpdateTask() {
        taskManager.createTask(task1);
        task1.setTitle("Updated Task 1");
        task1.setDescription("Updated Description");
        taskManager.updateTask(task1);
        assertEquals("Updated Task 1", taskManager.getTaskById(task1.getId()).getTitle(), "Task title should be updated");
        assertEquals("Updated Description", taskManager.getTaskById(task1.getId()).getDescription(), "Task description should be updated");
    }

    @Test
    void testUpdateEpic() {
        taskManager.createEpic(epic1);
        epic1.setTitle("Updated Epic 1");
        taskManager.updateEpic(epic1);
        assertEquals("Updated Epic 1", taskManager.getEpicById(epic1.getId()).getTitle(), "Epic title should be updated");
    }

    @Test
    void testUpdateSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask);
        subtask.setTitle("Updated Subtask 1");
        taskManager.updateSubTask(subtask);
        assertEquals("Updated Subtask 1", taskManager.getSubTaskById(subtask.getId()).getTitle(), "Subtask title should be updated");
    }

    @Test
    void testGetAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "There should be 2 tasks");
        assertTrue(tasks.contains(task1), "Task 1 should be in the list");
        assertTrue(tasks.contains(task2), "Task 2 should be in the list");
    }

    @Test
    void testGetAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(2, epics.size(), "There should be 2 epics");
        assertTrue(epics.contains(epic1), "Epic 1 should be in the list");
        assertTrue(epics.contains(epic2), "Epic 2 should be in the list");
    }

    @Test
    void testGetAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        List<SubTask> subtasks = taskManager.getAllSubtasks();
        assertEquals(2, subtasks.size(), "There should be 2 subtasks");
        assertTrue(subtasks.contains(subtask), "Subtask 1 should be in the list");
        assertTrue(subtasks.contains(subtask2), "Subtask 2 should be in the list");
    }

    @Test
    void testGetHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertTrue(history.contains(task1), "History should contain Task 1");
        assertTrue(history.contains(task2), "History should contain Task 2");
    }

    @Test
    void testEpicSubtaskListAfterSubtaskRemoval() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        epic1.addSubtask(subtask);
        epic1.addSubtask(subtask2);
        System.out.println(subtask.getId());
        System.out.println(subtask2.getId());
        assertEquals(2, epic1.getSubtasks().size(), "Epic should initially contain 2 subtasks");
        taskManager.removeSubTaskById(subtask.getId());
        System.out.println(epic1.getSubtasks());
        boolean containsRemoved = epic1.getSubtasks().stream().anyMatch(s -> s.getId() == subtask.getId());
        assertFalse(containsRemoved, "Epic should not contain the removed subtask");
    }

    @Test
    void testSettersDoNotBreakTaskIntegrity() {
        taskManager.createTask(task1);
        task1.setTitle("Changed Title");
        task1.setDescription("Changed Description");
        taskManager.updateTask(task1);
        Task retrieved = taskManager.getTaskById(task1.getId());
        assertEquals("Changed Title", retrieved.getTitle(), "Title should be updated");
        assertEquals("Changed Description", retrieved.getDescription(), "Description should be updated");
    }
}

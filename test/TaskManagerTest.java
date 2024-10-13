

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
    void testTaskManagerAddsTasksAndFindsById() {
        Task task = new Task("Task 1", "Description 1");
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
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getTitle(), retrievedTask.getTitle());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }
    @Test
    void testCreateTask() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);

        assertNotNull(taskManager.getTaskById(task.getId()), "Task should be created and retrievable");
        assertEquals("Task 1", taskManager.getTaskById(task.getId()).getTitle(), "Task title should match");
    }
    @Test
    void testCreateEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        assertNotNull(taskManager.getEpicById(epic.getId()), "Epic should be created and retrievable");
        assertEquals("Epic 1", taskManager.getEpicById(epic.getId()).getTitle(), "Epic title should match");
    }

    @Test
    void testCreateSubtask() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getSubTaskById(subtask.getId()), "Subtask should be created and retrievable");
        assertEquals("Subtask 1", taskManager.getSubTaskById(subtask.getId()).getTitle(), "Subtask title should match");
    }

    @Test
    void testRemoveTaskById() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()), "Task should be removed");
    }

    @Test
    void testRemoveEpicById() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);
        taskManager.removeEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()), "Epic should be removed");
    }

    @Test
    void testRemoveSubtaskById() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.removeSubTaskById(subtask.getId());

        assertNull(taskManager.getSubTaskById(subtask.getId()), "Subtask should be removed");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);

        task.setTitle("Updated Task 1");
        task.setDescription("Updated Description");
        taskManager.updateTask(task);

        assertEquals("Updated Task 1", taskManager.getTaskById(task.getId()).getTitle(), "Task title should be updated");
        assertEquals("Updated Description", taskManager.getTaskById(task.getId()).getDescription(), "Task description should be updated");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        epic.setTitle("Updated Epic 1");
        taskManager.updateEpic(epic);

        assertEquals("Updated Epic 1", taskManager.getEpicById(epic.getId()).getTitle(), "Epic title should be updated");
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.createSubtask(subtask);

        subtask.setTitle("Updated Subtask 1");
        taskManager.updateSubTask(subtask);

        assertEquals("Updated Subtask 1", taskManager.getSubTaskById(subtask.getId()).getTitle(), "Subtask title should be updated");
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "There should be 2 tasks");
        assertTrue(tasks.contains(task1), "Task 1 should be in the list");
        assertTrue(tasks.contains(task2), "Task 2 should be in the list");
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(2, epics.size(), "There should be 2 epics");
        assertTrue(epics.contains(epic1), "Epic 1 should be in the list");
        assertTrue(epics.contains(epic2), "Epic 2 should be in the list");
    }

    @Test
    void testGetAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        SubTask subtask1 = new SubTask("Subtask 1", "Description 1", epic.getId());
        SubTask subtask2 = new SubTask("Subtask 2", "Description 2", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<SubTask> subtasks = taskManager.getAllSubtasks();
        assertEquals(2, subtasks.size(), "There should be 2 subtasks");
        assertTrue(subtasks.contains(subtask1), "Subtask 1 should be in the list");
        assertTrue(subtasks.contains(subtask2), "Subtask 2 should be in the list");
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Добавляем задачи в историю
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertTrue(history.contains(task1), "History should contain Task 1");
        assertTrue(history.contains(task2), "History should contain Task 2");
    }

}

import interfaces.HistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {
    private InMemoryTaskManager taskManager;
    @BeforeEach
    void setUp() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();
    }
    @Test
    void testHistoryManagerRetainsPreviousVersions() {
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getId());


        List<Task> history = taskManager.getHistory();


        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertEquals(2, history.size());
    }
    @Test
    void testHistoryContainsMaxTenTasks() {
        HistoryManager historyManager = Managers.getDefaultHistory();


        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }


        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "history should contain at most 10 tasks");


        for (int i = 2; i <= 11; i++) {
            assertEquals("Task " + i, history.get(i - 2).getTitle(),
                    "history should contain tasks with IDs from 2 to 11");
        }
    }
}
import interfaces.HistoryManager;
import managers.Managers;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }


    @Test
    void testHistoryManagerRetainsPreviousVersions() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertTrue(history.contains(task1), "History should contain task 1");
        assertTrue(history.contains(task2), "History should contain task 2");
        assertEquals(2, history.size(), "History should have 2 tasks");
    }

    @Test
    void testHistoryContainsMaxTenTasks() {
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "History should contain at most 10 tasks");

        for (int i = 2; i <= 11; i++) {
            assertEquals("Task " + i, history.get(i - 2).getTitle(),
                    "History should contain tasks with IDs from 2 to 11");
        }
    }


    @Test
    void testRemoveTaskFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task1), "History should not contain task 1");
        assertTrue(history.contains(task2), "History should contain task 2");
        assertEquals(1, history.size(), "History should contain 1 task after removal");
    }


    @Test
    void testTaskRemovalConsistency() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);


        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertFalse(history.contains(task1), "History should not contain task 1 after removal");
        assertTrue(history.contains(task2), "History should still contain task 2");
    }

    @Test
    void testTaskUpdateDoesNotCorruptHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        historyManager.add(task1);
        task1.setDescription("Updated Description");

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History should only have one task after update");
        assertEquals("Updated Description", history.get(0).getDescription(),
                "Task description should be updated");
    }
}

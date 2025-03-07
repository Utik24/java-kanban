import interfaces.HistoryManager;
import managers.Managers;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
    }


    @Test
    void testHistoryManagerRetainsPreviousVersions() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertTrue(history.contains(task1), "History should contain task 1");
        assertTrue(history.contains(task2), "History should contain task 2");
        assertEquals(2, history.size(), "History should have 2 tasks");
    }


    @Test
    void testRemoveTaskFromHistory() {
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
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task1), "History should not contain task 1 after removal");
        assertTrue(history.contains(task2), "History should still contain task 2");
    }

    @Test
    void testTaskUpdateDoesNotCorruptHistory() {
        historyManager.add(task1);
        task1.setDescription("Updated Description");
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "History should only have one task after update");
        assertEquals("Updated Description", history.get(0).getDescription(),
                "Task description should be updated");
    }

    @Test
    void testRemoveFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(List.of(task2, task3), history, "История должна содержать 2 задачи после удаления из начала");
    }

    // Удаление задачи из середины истории
    @Test
    void testRemoveFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task1, task3), history, "История должна содержать 2 задачи после удаления из середины");
    }

    // Удаление задачи из конца истории
    @Test
    void testRemoveFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task1, task2), history, "История должна содержать 2 задачи после удаления из конца");

    }
}

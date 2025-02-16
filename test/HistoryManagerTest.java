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

    @Test
    void testRemoveFromBeginning() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
        Task task4 = new Task("Task 4", "Description 4");
        task4.setId(4);
        Task task5 = new Task("Task 5", "Description 5");
        task5.setId(5);


        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);


        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(4, history.size(), "История должна содержать 4 задачи после удаления из начала");
        assertEquals(task2, history.get(0), "Первый элемент должен быть task2");
        assertEquals(task3, history.get(1), "Второй элемент должен быть task3");
        assertEquals(task4, history.get(2), "Третий элемент должен быть task4");
        assertEquals(task5, history.get(3), "Четвёртый элемент должен быть task5");
    }

    // Удаление задачи из середины истории
    @Test
    void testRemoveFromMiddle() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
        Task task4 = new Task("Task 4", "Description 4");
        task4.setId(4);
        Task task5 = new Task("Task 5", "Description 5");
        task5.setId(5);


        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);


        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(4, history.size(), "История должна содержать 4 задачи после удаления из середины");
        assertEquals(task1, history.get(0), "Первый элемент должен быть task1");
        assertEquals(task2, history.get(1), "Второй элемент должен быть task2");
        assertEquals(task4, history.get(2), "Третий элемент должен быть task4");
        assertEquals(task5, history.get(3), "Четвёртый элемент должен быть task5");
    }

    // Удаление задачи из конца истории
    @Test
    void testRemoveFromEnd() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
        Task task4 = new Task("Task 4", "Description 4");
        task4.setId(4);
        Task task5 = new Task("Task 5", "Description 5");
        task5.setId(5);


        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);


        historyManager.remove(task5.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "История должна содержать 4 задачи после удаления из конца");
        assertEquals(task1, history.get(0), "Первый элемент должен быть task1");
        assertEquals(task2, history.get(1), "Второй элемент должен быть task2");
        assertEquals(task3, history.get(2), "Третий элемент должен быть task3");
        assertEquals(task4, history.get(3), "Четвёртый элемент должен быть task4");
    }
}

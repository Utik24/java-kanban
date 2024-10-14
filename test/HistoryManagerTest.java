import interfaces.HistoryManager;
import managers.Managers;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void testHistoryManagerRetainsPreviousVersions() {
        Task task1 = new Task("Task 1", "Description 1");


        Task task2 = new Task("Task 2", "Description 2");
        historyManager.add(task1);
        historyManager.add(task2);


        List<Task> history = historyManager.getHistory();


        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertEquals(2, history.size());
    }

    @Test
    void testHistoryContainsMaxTenTasks() {


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
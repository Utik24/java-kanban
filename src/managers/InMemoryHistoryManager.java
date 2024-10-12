package managers;

import interfaces.HistoryManager;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyTask;

    public InMemoryHistoryManager() {
        this.historyTask = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (historyTask.size() >= 10) {
            historyTask.remove(0);
        }
        historyTask.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }
}

import java.util.HashMap;

public class DataTask {
    private HashMap<Long, Task> tasks = new HashMap<>();
    private HashMap<Long, Task> subtasks = new HashMap<>();
    private HashMap<Long, Task> epics = new HashMap<>();

    public HashMap<Long, Task> getTasks() {
        return tasks;
    }

    public HashMap<Long, Task> getSubtasks() {
        return subtasks;
    }

    public HashMap<Long, Task> getEpics() {
        return epics;
    }
}

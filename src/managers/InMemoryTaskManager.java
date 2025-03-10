package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subtasks;
    private HistoryManager historyManager;
    private int currentId;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.currentId = 1;
    }


    private int generateId() {
        return currentId++;
    }

    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            if (getHistory().contains(task)) {
                historyManager.remove(task.getId());
            }
        }
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (SubTask subtask : subtasks.values()) {
            subtasks.remove(subtask.getId());
            if (getHistory().contains(subtask)) {
                historyManager.remove(subtask.getId());
            }
        }
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            for (SubTask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            if (getHistory().contains(epic)) {
                historyManager.remove(epic.getId());
            }
        }
        epics.clear();
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(SubTask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>(tasks.values());
        for (Task task : list) {
            historyManager.add(task);
        }
        return list;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> list = new ArrayList<>(epics.values());
        for (Epic epic : list) {
            historyManager.add(epic);
        }
        return list;
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        List<SubTask> list = new ArrayList<>(subtasks.values());
        for (SubTask subtask : list) {
            historyManager.add(subtask);
        }
        return list;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task getSubTaskById(int id) {
        SubTask subtask = subtasks.get(id);
        historyManager.add(subtask); // Добавляем в историю
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
            }
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        if (subtasks.containsKey(id)) {
            SubTask subtask = subtasks.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtasks().removeIf(s -> s.getId() == subtask.getId());
                if (getHistory().contains(subtask)) {
                    historyManager.remove(id);
                }
                epic.updateStatus();
            }

        }
    }

    @Override
    public void removeTaskById(int id) {
        Task task = tasks.remove(id);
        if (getHistory().contains(task)) {
            historyManager.remove(id);
        }
    }


    @Override
    public void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);


            for (SubTask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
            }
            if (getHistory().contains(epic)) {
                historyManager.remove(id);
            }
        }
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtasks();
        }
        return null;
    }
}

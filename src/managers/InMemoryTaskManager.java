package managers;

import exceptions.IntersectionException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import validations.TaskIntersectionValidation;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, SubTask> subtasks;
    protected TaskIntersectionValidation taskIntersectionValidation;
    protected TreeSet<Task> prioritizedTasks;
    private HistoryManager historyManager;
    private int currentId;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.currentId = 1;
        taskIntersectionValidation = new TaskIntersectionValidation();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }


    private int generateId() {
        return currentId++;
    }

    @Override
    public void removeAllTasks() {
        tasks.values().stream()
                .filter(task -> getHistory().contains(task))
                .forEach(task -> {
                    historyManager.remove(task.getId());
                    taskIntersectionValidation.removeTask(task.getStartTime(), task.getDuration());
                    prioritizedTasks.remove(task);
                });
        tasks.clear();
    }


    @Override
    public void removeAllSubTasks() {
        List<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer id : subtaskIds) {
            SubTask subtask = subtasks.get(id);
            subtasks.remove(id);
            if (getHistory().contains(subtask)) {
                historyManager.remove(subtask.getId());
            }
            taskIntersectionValidation.removeTask(subtask.getStartTime(), subtask.getDuration());
            prioritizedTasks.remove(subtask);
        }
        epics.values().forEach(epic -> epic.getSubtasks().clear());
        subtasks.clear();
    }


    @Override
    public void removeAllEpics() {
        epics.values()
                .forEach(epic -> {
                    epic.getSubtasks().forEach(subtask -> {
                        taskIntersectionValidation.removeTask(subtask.getStartTime(), subtask.getDuration());
                        subtasks.remove(subtask.getId());
                        historyManager.remove(subtask.getId());
                    });
                    if (getHistory().contains(epic)) {
                        historyManager.remove(epic.getId());
                    }
                });
        epics.clear();
    }


    @Override
    public void createTask(Task task) throws IntersectionException {
        if (taskIntersectionValidation.addTask(task.getStartTime(), task.getDuration())) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            throw new IntersectionException("Задачи пересекаются");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epic.updateTimeFields();
        epic.updateStatus();
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(SubTask subtask) throws IntersectionException {
        if (taskIntersectionValidation.addTask(subtask.getStartTime(), subtask.getDuration())) {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask);
            }
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        } else {
            throw new IntersectionException("Задачи пересекаются");
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
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
    public void updateTask(Task task) throws IntersectionException {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            if (taskIntersectionValidation.updateTask(oldTask.getStartTime(), oldTask.getDuration(), task.getStartTime(), task.getDuration())) {
                prioritizedTasks.remove(oldTask);
                prioritizedTasks.add(task);
                tasks.put(task.getId(), task);
            } else {
                throw new IntersectionException("Задачи пересекаются");
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) throws IntersectionException {
        if (subtasks.containsKey(subtask.getId())) {
            SubTask oldSubtask = subtasks.get(subtask.getId());
            if (taskIntersectionValidation.updateTask(oldSubtask.getStartTime(), oldSubtask.getDuration(), subtask.getStartTime(), subtask.getDuration())) {
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.remove(oldSubtask);
                prioritizedTasks.add(subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.updateStatus();
                    epic.updateTimeFields();
                }
            } else {
                throw new IntersectionException("Задачи пересекаются");
            }

        }
    }

    @Override
    public void removeSubTaskById(int id) {
        Optional.ofNullable(subtasks.remove(id))
                .ifPresent(subtask -> {
                    taskIntersectionValidation.removeTask(subtask.getStartTime(), subtask.getDuration());
                    prioritizedTasks.remove(subtask);
                    Epic epic = epics.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.getSubtasks().removeIf(s -> s.getId() == subtask.getId());
                        if (getHistory().contains(subtask)) {
                            historyManager.remove(id);
                        }
                        epic.updateStatus();
                        epic.updateTimeFields();
                    }
                });
    }


    @Override
    public void removeTaskById(int id) {
        Optional.ofNullable(tasks.remove(id))
                .ifPresent(task -> {
                    taskIntersectionValidation.removeTask(task.getStartTime(), task.getDuration());
                    if (getHistory().contains(task)) {
                        prioritizedTasks.remove(task);
                        historyManager.remove(id);
                    }
                });
    }


    @Override
    public void removeEpicById(int id) {
        Optional.ofNullable(epics.remove(id))
                .ifPresent(epic -> {
                    epic.getSubtasks().forEach(subtask -> {
                        taskIntersectionValidation.removeTask(subtask.getStartTime(), subtask.getDuration());
                        prioritizedTasks.remove(subtask);
                        subtasks.remove(subtask.getId());
                        historyManager.remove(subtask.getId());
                    });
                    if (getHistory().contains(epic)) {
                        historyManager.remove(id);
                    }
                });
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<SubTask> getSubtasksByEpicId(int epicId) {
        return Optional.ofNullable(epics.get(epicId))
                .map(Epic::getSubtasks)
                .orElse(Collections.emptyList());
    }


    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

}

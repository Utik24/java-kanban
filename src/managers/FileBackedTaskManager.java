package managers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static utility.StringUtility.fromString;
import static utility.StringUtility.taskToString;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task.getTaskType().equals(TaskType.EPIC)) {
                    if (!manager.epics.containsKey(task.getId())) {
                        manager.epics.put(task.getId(), (Epic) task);
                    }
                } else if (task.getTaskType().equals(TaskType.SUBTASK)) {
                    if (!manager.subtasks.containsKey(task.getId())) {
                        manager.subtasks.put(task.getId(), (SubTask) task);
                        manager.epics.get(((SubTask) task).getEpicId()).addSubtask((SubTask) task);
                        manager.prioritizedTasks.add(task);
                    }
                } else {
                    manager.tasks.put(task.getId(), task);
                    manager.prioritizedTasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading tasks from file", e);
        }
        return manager;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            writer.write("id,type,name,status,description,epic,duration,startTime\n");
            for (Task task : tasks.values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (SubTask subtask : subtasks.values()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач в файл", e);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(SubTask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }
}

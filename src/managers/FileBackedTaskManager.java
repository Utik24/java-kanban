package managers;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import model.TaskType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : super.tasks.values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : super.epics.values()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (SubTask subtask : super.subtasks.values()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof SubTask) {
                    manager.subtasks.put(task.getId(), (SubTask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач из файла", e);
        }
        return manager;
    }


    private static String taskToString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getTaskType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                (task instanceof SubTask) ? ((SubTask) task).getEpicId() : "");
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (taskType) {
            case TASK:
                return new Task(id,taskType, name, status, description);
            case EPIC:
                return new Epic(id,taskType, name, status, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new SubTask(id,taskType, name, status, description, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + taskType);
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

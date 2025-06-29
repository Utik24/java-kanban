package interfaces;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {


    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(SubTask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subtask);

    void removeTaskById(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int id);

    List<Task> getHistory();

    List<SubTask> getSubtasksByEpicId(int epicId);

    List<Task> getPrioritizedTasks();
}

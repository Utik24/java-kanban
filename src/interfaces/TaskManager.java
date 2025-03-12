package interfaces;

import exceptions.IntersectionException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {


    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    void createTask(Task task) throws IntersectionException;

    void createEpic(Epic epic);

    void createSubtask(SubTask subtask) throws IntersectionException;

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubtasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Task getSubTaskById(int id);

    void updateTask(Task task) throws IntersectionException;

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subtask) throws IntersectionException;

    void removeTaskById(int id);

    void removeSubTaskById(int id);

    void removeEpicById(int id);

    List<Task> getHistory();

    List<SubTask> getSubtasksByEpicId(int epicId);

    List<Task> getPrioritizedTasks();
}

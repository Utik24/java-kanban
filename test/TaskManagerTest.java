import interfaces.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager = (T) createTaskManager();
    protected Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
    protected Epic epic = new Epic("Epic 1", "Epic Description");
    protected SubTask subTask = new SubTask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));

    // Метод для создания конкретного менеджера задач
    public abstract TaskManager createTaskManager();

    @Test
    void shouldCreateTask() {
        taskManager.createTask(task);
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    void shouldCreateEpic() {
        taskManager.createEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void shouldCreateSubTaskAndLinkToEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        epic.addSubtask(subTask);
        // Проверяем что подзадача добавлена к эпику
        assertTrue(epic.getSubtasks().contains(subTask));
    }

    @Test
    void shouldUpdateEpicStatusBasedOnSubtasks() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        epic.addSubtask(subTask);
        // Поставим статус подзадачи в "Завершено"
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);

        // Проверим что статус эпика изменился
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldDetectTaskIntersection() {
        taskManager.createTask(task);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 5));
        taskManager.createTask(task2);

        // Проверяем что пересечение задач обнаружено
        assertEquals(1, taskManager.getAllTasks().size(), "Должна быть только одна задача, так как они пересекаются.");
    }

    @Test
    void shouldRemoveTask() {
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void shouldRemoveSubTask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        taskManager.removeSubTaskById(subTask.getId());

        assertTrue(epic.getSubtasks().isEmpty(), "Подзадача должна быть удалена из эпика");
    }

    @Test
    void shouldRemoveEpicAndRelatedSubtasks() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        epic.addSubtask(subTask);
        taskManager.removeEpicById(epic.getId());

        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи должны быть удалены");
    }


}

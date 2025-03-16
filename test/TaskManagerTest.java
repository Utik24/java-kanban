import exceptions.IntersectionException;
import interfaces.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected Task task = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
    protected Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(15), LocalDateTime.of(2023, 1, 1, 0, 0));
    protected Epic epic = new Epic("Epic 1", "Epic Description");
    protected SubTask subTask = new SubTask("Subtask 1", "Subtask Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
    protected SubTask subTask2 = new SubTask("Subtask 2", "Subtask Description 2", Duration.ofMinutes(15), LocalDateTime.of(2024, 1, 1, 0, 0));

    protected T taskManager;

    // Метод для создания конкретной реализации менеджера задач
    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    protected void settings() throws IOException {
        taskManager = createTaskManager();
    }

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
        IntersectionException exception = assertThrows(
                IntersectionException.class,
                () -> taskManager.createTask(task2),
                "Задачи пересекаются"
        );

        assertEquals("Задачи пересекаются", exception.getMessage());

        // Проверяем, что в менеджере осталась только первая задача
        assertEquals(1, taskManager.getAllTasks().size(), "Ожидается только одна задача");
    }

    @Test
    void shouldRemoveTask() {
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void shouldRemoveAllTask() {
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();

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
    void shouldRemoveAllSubTask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask2);
        taskManager.removeAllSubTasks();

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

    @Test
    void shouldRemoveAllEpicAndRelatedSubtasks() {
        taskManager.createEpic(epic);
        Epic epic2 = new Epic("Epic 2", "Epic Description 2");
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subTask);
        taskManager.createSubtask(subTask2);
        epic.addSubtask(subTask);
        epic2.addSubtask(subTask2);
        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи должны быть удалены");
    }

    @Test
    void getAllTasks_shouldReturnEmptyListWhenNoTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void getAllEpics_shouldReturnEmptyListWhenNoEpics() {
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void getAllSubtasks_shouldReturnEmptyListWhenNoSubtasks() {
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void getTaskById_shouldReturnTaskWhenExists() {
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    void getEpicById_shouldReturnEpicWhenExists() {
        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getId(), retrievedEpic.getId());
    }

    @Test
    void getSubTaskById_shouldReturnSubtaskWhenExists() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        SubTask retrievedSubtask = (SubTask) taskManager.getSubTaskById(subTask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subTask.getId(), retrievedSubtask.getId());
    }

    @Test
    void updateTask_shouldUpdateTask() {
        taskManager.createTask(task);
        task.setTitle("Updated Task");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle());
    }

    @Test
    void updateEpic_shouldUpdateEpic() {
        taskManager.createEpic(epic);
        epic.setTitle("Updated Epic");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Updated Epic", updatedEpic.getTitle());
    }

    @Test
    void updateSubTask_shouldUpdateSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        subTask.setTitle("Updated Subtask");
        taskManager.updateSubTask(subTask);
        SubTask updatedSubtask = (SubTask) taskManager.getSubTaskById(subTask.getId());
        assertEquals("Updated Subtask", updatedSubtask.getTitle());
    }

    @Test
    void removeTaskById_shouldRemoveTask() {
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void removeSubTaskById_shouldRemoveSubtask() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        taskManager.removeSubTaskById(subTask.getId());
        assertNull(taskManager.getSubTaskById(subTask.getId()));
    }

    @Test
    void removeEpicById_shouldRemoveEpic() {
        Epic epic = new Epic("Epic to Remove", "Description");
        taskManager.createEpic(epic);
        taskManager.removeEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void getHistory_shouldReturnEmptyListInitially() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void getSubtasksByEpicId_shouldReturnSubtasksWhenPresent() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subTask);
        epic.addSubtask(subTask);
        List<SubTask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
    }

    @Test
    void getPrioritizedTasks_shouldReturnSortedTasks() {
        taskManager.createTask(task);
        taskManager.createTask(task2);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(task2, prioritizedTasks.get(1));
        assertEquals(task, prioritizedTasks.get(0));
    }

    @Test
    void testGetPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(15), LocalDateTime.of(2023, 1, 1, 0, 0));
        Task task3 = new Task("Task 3", "Description 3", Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println(taskManager.getPrioritizedTasks());
        assertEquals(taskManager.getPrioritizedTasks(), List.of(task3, task1, task2));
    }


    @Test
    void testIdConflictInTaskManager() {
        task.setId(1);
        taskManager.createTask(task);
        task2.setId(1);
        taskManager.createTask(task2);
        assertEquals(task, taskManager.getTaskById(1));
        assertNotEquals(task2, taskManager.getTaskById(1));
    }

    @Test
    void testTaskImmutabilityOnAddition() {
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getTitle(), retrievedTask.getTitle());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }


}

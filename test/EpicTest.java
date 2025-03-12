import model.Epic;
import model.Status;
import model.SubTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest {
    private Epic epic;
    private SubTask subTaskNew;
    private SubTask subTaskNew2;
    private SubTask subTaskDone;
    private SubTask subTaskDone2;
    private SubTask subTaskInProgress;

    @BeforeEach
    public void setup() {
        epic = new Epic("Epic 1", "Epic Description");
        epic.setId(0);
        subTaskNew = new SubTask("Subtask 1", Status.NEW, "Subtask Description", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subTaskNew.setId(1);
        subTaskNew2 = new SubTask("Subtask 1-2", Status.NEW, "Subtask Description", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2021, 1, 1, 0, 0));
        subTaskNew2.setId(2);
        subTaskDone = new SubTask("Subtask 2", Status.DONE, "Subtask Description 2", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subTaskDone.setId(3);
        subTaskDone2 = new SubTask("Subtask 2-2", Status.DONE, "Subtask Description 2", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subTaskDone2.setId(4);
        subTaskInProgress = new SubTask("Subtask 3", Status.IN_PROGRESS, "Subtask Description 3", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subTaskInProgress.setId(5);
    }

    @Test
    void testGetEpicStartTime() {
        epic.addSubtask(subTaskNew);
        epic.addSubtask(subTaskNew2);
        System.out.println(epic.getStartTime());
        assertEquals(epic.getStartTime(), subTaskNew2.getStartTime());
    }

    @Test
    void testEpicCannotAddItselfAsSubTask() {
        epic.setId(0);
        subTaskNew.setId(0);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(subTaskNew);
        });
        assertEquals("Subtask can't be Epic", thrown.getMessage());
    }

    @Test
    public void testUpdateStatus_AllSubtasksNew() {
        epic.addSubtask(subTaskNew);
        epic.addSubtask(subTaskNew2);
        epic.updateStatus();

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void testUpdateStatus_AllSubtasksDone() {
        epic.addSubtask(subTaskDone);
        epic.addSubtask(subTaskDone);
        epic.updateStatus();

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testUpdateStatus_NewAndDoneSubtasks() {
        epic.addSubtask(subTaskNew);
        epic.addSubtask(subTaskDone);
        epic.updateStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void testUpdateStatus_AllSubtasksInProgress() {
        epic.addSubtask(subTaskInProgress);
        epic.addSubtask(subTaskInProgress);
        epic.updateStatus();

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}
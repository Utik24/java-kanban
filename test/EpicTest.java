import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EpicTest {

    @Test
    void testEpicCannotAddItselfAsSubTask() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        epic.setId(1);

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId());
        subtask.setId(1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(subtask);
        });
        assertEquals("Subtask can't be Epic", thrown.getMessage());
    }
}
import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubTaskTest {
    @Test
    void testSubTaskEqualityById() {
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", 1);
        subTask1.setId(1);
        SubTask subTask2 = new SubTask("SubTask 1", "Description 1", 1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2);
    }

    @Test
    void testSubTaskCannotBeItsOwnEpic() {
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
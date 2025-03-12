import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubTaskTest {
    @Test
    void testSubTaskEqualityById() {
        SubTask subTask1 = new SubTask("Subtask 1", "Subtask Description", 1, Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subTask1.setId(1);
        SubTask subTask2 = new SubTask("Subtask 2", "Subtask Description", 2, Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subTask2.setId(1);
        assertEquals(subTask1, subTask2);
    }

    @Test
    void testSubTaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        epic.setId(1);

        SubTask subtask = new SubTask("Subtask 1", "Subtask Description", epic.getId(), Duration.ofMinutes(15), LocalDateTime.of(2022, 1, 1, 0, 0));
        subtask.setId(1);


        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            epic.addSubtask(subtask);
        });

        assertEquals("Subtask can't be Epic", thrown.getMessage());
    }
}
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subtasks;

    public Epic(String title, String description, int id) {
        super(title, description, id, Status.NEW);
        this.subtasks = new ArrayList<>();
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(SubTask subtask) {
        subtasks.add(subtask);
        updateStatus();  // Обновляем статус эпика при добавлении новой подзадачи
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            this.status = Status.NEW;
            return;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (SubTask subtask : subtasks) {
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            this.status = Status.DONE;
        } else if (allNew) {
            this.status = Status.NEW;
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }
}

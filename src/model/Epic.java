package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subtasks;


    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
    }

    public Epic(String title, String description, List<SubTask> subtasks) {
        super(title, description);
        this.subtasks = subtasks;
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(SubTask subtask) {
        if (subtask.getEpicId() == this.id) {
            throw new IllegalArgumentException("Subtask can't be Epic");
        }
        subtasks.add(subtask);
        updateStatus();
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

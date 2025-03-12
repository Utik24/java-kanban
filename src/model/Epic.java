package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<SubTask> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        updateTimeFields();
    }

    public Epic(int id, String title, Status status, String description) {
        super(id, title, status, description);
        this.subtasks = new ArrayList<>();
        updateTimeFields();
    }

    public Epic(String title, String description, List<SubTask> subtasks) {
        super(title, description);
        this.subtasks = subtasks;
        updateTimeFields();
    }

    public List<SubTask> getSubtasks() {
        return subtasks;
    }

    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void addSubtask(SubTask subtask) {
        if (subtask.getId() == this.id) {
            throw new IllegalArgumentException("Subtask can't be Epic");
        }
        subtasks.add(subtask);
        subtask.setEpicId(this.id);
        updateStatus();
        updateTimeFields();
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

    public void updateTimeFields() {
        this.startTime = subtasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        this.endTime = subtasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        this.duration = subtasks.stream()
                .map(SubTask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}

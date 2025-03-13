package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
    }

    public SubTask(String title, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, Status status, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, title, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String title, Status status, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(title, status, description, duration, startTime);
        this.epicId = epicId;
    }

    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epicId=" + epicId +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}

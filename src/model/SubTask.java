package model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description) {
        super(title, description);
    }

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, Status status, String description, int epicId) {
        super(id, title, status, description);
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
                '}';
    }
}

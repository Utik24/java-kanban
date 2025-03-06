package model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description) {
        super(title, description);
    }

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.taskType=TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public SubTask(int id, TaskType taskType, String title, Status status, String description, int epicId) {
        super(id, taskType, title, status, description);
        this.epicId = epicId;
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

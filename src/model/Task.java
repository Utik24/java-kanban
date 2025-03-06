package model;

import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    protected TaskType taskType;
    //поля protected тк они необходимы для наследования private поля не наследуются

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.taskType=TaskType.TASK;
        this.status = Status.NEW;
    }

    public Task(int id, TaskType taskType, String title, Status status, String description) {
        this.id = id;
        this.title = title;
        this.taskType = taskType;
        this.description = description;
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Model.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}

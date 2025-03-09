package utility;

import model.*;

import java.util.Optional;

public class StringUtility {
    public static String taskToString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getTaskType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                Optional.of(task)
                        .flatMap(t -> Optional.ofNullable(getEpicIdIfSubTask(t)))
                        .map(String::valueOf)
                        .orElse("")
        );
    }


    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType taskType = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        return switch (taskType) {
            case TASK -> new Task(id, name, status, description);
            case EPIC -> new Epic(id, name, status, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                yield new SubTask(id, name, status, description, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + taskType);
        };
    }

    private static Integer getEpicIdIfSubTask(Task task) {
        return Optional.of(task)
                .filter(SubTask.class::isInstance)
                .map(SubTask.class::cast)
                .map(SubTask::getEpicId)
                .orElse(null);
    }
}

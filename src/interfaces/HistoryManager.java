package interfaces;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task); // Добавляет задачу в историю

    List<Task> getHistory(); // Возвращает список просмотренных задач
}

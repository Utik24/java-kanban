package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import interfaces.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            // Получаем приоритетные задачи
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String response = prioritizedTasks.toString();  // Преобразуем список задач в JSON
            sendText(exchange, response, 200);  // Отправляем ответ с приоритетными задачами
        }
    }
}

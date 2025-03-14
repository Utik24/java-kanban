package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionException;
import interfaces.TaskManager;
import model.Task;
import utility.ConverterJsonToTask;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            Task task = ConverterJsonToTask.converterTaskFromJson(exchange);
            try {
                createTaskOrUpdateTask(task, exchange);
            } catch (JsonSyntaxException e) {
                System.err.println("JSON Syntax Error: " + e.getMessage());
                sendInternalError(exchange);
            }
        } else if ("GET".equals(method)) {
            showAllOrIdTasks(exchange);
        } else if ("DELETE".equals(method)) {
            if (getId(exchange) != null) {
                deleteTask(exchange);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);  // Метод не поддерживается
        }
    }

    public void createTaskOrUpdateTask(Task task, HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 3) {
            try {
                int id = getId(exchange);
                task.setId(id);
                taskManager.updateTask(task);
                response = "Задача обновлена успешно! Task ID: " + task.getId();
            } catch (IntersectionException e) {
                sendHasInteractions(exchange);
            }
        } else {
            taskManager.createTask(task);
            response = "Задача создана успешно! Task ID: " + task.getId();
        }
        sendText(exchange, response, 201);
    }

    public void showAllOrIdTasks(HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length != 3) {
            response = "Все задачи: \n" + taskManager.getAllTasks().toString();
        } else {
            int id = getId(exchange);
            if (taskManager.getTaskById(id) != null) {
                response = "Задача под Id: " + id + "\n" + taskManager.getTaskById(id);
            } else {
                sendNotFound(exchange);
            }
        }
        sendText(exchange, response, 200);
    }

    public void deleteTask(HttpExchange exchange) throws IOException {
        String response;
        String[] path = exchange.getRequestURI().getPath().split("/");
        int id = getId(exchange);
        taskManager.removeTaskById(id);
        response = "Задача удалена успешно!";
        sendText(exchange, response, 200);
    }
}

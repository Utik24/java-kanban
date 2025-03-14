package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionException;
import interfaces.TaskManager;
import model.SubTask;
import utility.ConverterJsonToTask;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            SubTask subTask = ConverterJsonToTask.converterSubTaskFromJson(exchange);
            try {
                createSubTaskOrUpdateSubTask(subTask, exchange);
            } catch (JsonSyntaxException e) {
                System.err.println("JSON Syntax Error: " + e.getMessage());
                sendInternalError(exchange);
            }
        } else if ("GET".equals(method)) {
            showAllOrIdSubTasks(exchange);
        } else if ("DELETE".equals(method)) {
            if (getId(exchange) != null) {
                deleteSubTask(exchange);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);  // Метод не поддерживается
        }
    }

    public void createSubTaskOrUpdateSubTask(SubTask subTask, HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 3) {
            try {
                int id = getId(exchange);
                subTask.setId(id);
                taskManager.updateSubTask(subTask);
                response = "Подзадача обновлена успешно! Task ID: " + subTask.getId();
            } catch (IntersectionException e) {
                sendHasInteractions(exchange);
            }
        } else {
            taskManager.createSubtask(subTask);
            response = "Подзадача создана успешно! Task ID: " + subTask.getId();
        }
        sendText(exchange, response, 201);
    }

    public void showAllOrIdSubTasks(HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length != 3) {
            response = "все Подзадачи: \n" + taskManager.getAllSubtasks().toString();
        } else {
            int id = getId(exchange);
            if (taskManager.getSubTaskById(id) != null) {
                response = "Подзадача под Id: " + id + "\n" + taskManager.getSubTaskById(id);
            } else {
                sendNotFound(exchange);
            }
        }
        sendText(exchange, response, 200);
    }

    public void deleteSubTask(HttpExchange exchange) throws IOException {
        String response;
        int id = getId(exchange);
        taskManager.removeSubTaskById(id);
        response = "Подзадача удалена успешно!";
        sendText(exchange, response, 200);
    }
}

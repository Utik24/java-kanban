package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionException;
import interfaces.TaskManager;
import model.Epic;
import utility.ConverterJsonToTask;

import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            Epic epic = ConverterJsonToTask.converterEpicFromJson(exchange);
            try {
                createEpicOrUpdateEpic(epic, exchange);
            } catch (JsonSyntaxException e) {
                System.err.println("JSON Syntax Error: " + e.getMessage());
                sendInternalError(exchange);
            }
        } else if ("GET".equals(method)) {
            showAllOrIdEpics(exchange);
        } else if ("DELETE".equals(method)) {
            if (getId(exchange) != null) {
                deleteEpic(exchange);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);  // Метод не поддерживается
        }
    }

    public void createEpicOrUpdateEpic(Epic epic, HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 3) {
            try {
                int id = getId(exchange);
                epic.setId(id);
                taskManager.updateEpic(epic);
                response = "Подзадача обновлена успешно! Task ID: " + epic.getId();
            } catch (IntersectionException e) {
                sendHasInteractions(exchange);
            }
        } else {
            taskManager.createEpic(epic);
            response = "Подзадача создана успешно! Task ID: " + epic.getId();
        }
        sendText(exchange, response, 201);
    }

    public void showAllOrIdEpics(HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 2) {
            response = "все Подзадачи: \n" + taskManager.getAllEpics().toString();
        } else if (path.length == 4) {
            int id = getId(exchange);
            if (taskManager.getEpicById(id) != null) {
                response = "Все Подзадачи эпика с id: " + id + " \n" + taskManager.getSubTaskById(id);
            } else {
                sendNotFound(exchange);
            }
        } else {
            int id = getId(exchange);
            if (taskManager.getEpicById(id) != null) {
                response = "Подзадача под Id: " + id + "\n" + taskManager.getEpicById(id);
            } else {
                sendNotFound(exchange);
            }
        }
        sendText(exchange, response, 200);
    }

    public void deleteEpic(HttpExchange exchange) throws IOException {
        String response;
        int id = getId(exchange);
        taskManager.removeEpicById(id);
        response = "Подзадача удалена успешно!";
        sendText(exchange, response, 200);
    }
}

package server;

import com.sun.net.httpserver.HttpServer;
import interfaces.TaskManager;
import managers.InMemoryTaskManager;
import server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(new InMemoryTaskManager());
        server.initServer();
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void closeServer() {
        server.stop(1);
    }

    public void initServer() throws IOException {

        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));

        System.out.println("Сервер запущен на порту " + PORT);
        server.start();
    }
}

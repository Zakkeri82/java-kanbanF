package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SubtasksHandle implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandle(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "GET": {
                    if (Pattern.matches("^/subtasks$", path)) {
                        String response = gson.toJson(taskManager.getSubtasks());
                        sendText(httpExchange, response);
                        break;
                    }
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1 && taskManager.getSubtaskById(id) != null) {
                            Subtask subtask = taskManager.findSubtask(taskManager.getSubtaskById(id));
                            String response = gson.toJson(subtask);
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Не найдена подзадача с id - " + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                    } else {
                        System.out.println("Ожидается /subtasks/{id} запрос, а получили - " + path);
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    }
                    break;
                }
                case "POST": {
                    if (Pattern.matches("^/subtasks$", path)) {
                        Subtask subtask = gson.fromJson(readText(httpExchange), Subtask.class);
                        try {
                            taskManager.createSubtasks(subtask);
                            System.out.println("Подзадача успешно добавлена");
                            httpExchange.sendResponseHeaders(201, 0);
                        } catch (Error error) {
                            httpExchange.sendResponseHeaders(406, 0);
                            System.out.println("Подзадача не создана, т.к. пересекается с другой задачей");
                        }
                        break;
                    }
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1 && taskManager.getSubtaskById(id) != null) {
                            Subtask subtask = gson.fromJson(readText(httpExchange), Subtask.class);
                            try {
                                taskManager.updateSubtask(subtask);
                                System.out.println("Обновлена подзадача с id - " + id);
                                httpExchange.sendResponseHeaders(201, 0);
                            } catch (Error error) {
                                httpExchange.sendResponseHeaders(406, 0);
                                System.out.println("Подзадача не обновлена, т.к. пересекается с другой задачей");
                            }
                        } else {
                            System.out.println("Не найдена подзадача с id - " + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }

                    } else {
                        System.out.println("Ожидается /subtasks/{id} запрос, а получили - " + path);
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    }
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1 && taskManager.getSubtaskById(id) != null) {
                            taskManager.removeSubtask(taskManager.getSubtaskById(id));
                            System.out.println("Удалена подзадача с id - " + id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Не найдена подзадача с id - " + id);
                            httpExchange.sendResponseHeaders(404, 0);
                        }

                    } else {
                        System.out.println("Ожидается /subtasks/{id} запрос, а получили - " + path);
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                    break;
                }
                default: {
                    System.out.println("Ожидается GET, POST или DELETE запрос, а получили - " + method);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();

        } finally {
            httpExchange.close();
        }
    }

    private int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void sendText(HttpExchange exchange, String response) throws IOException {
        byte[] resp = response.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
    }

    private String readText(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), UTF_8);
    }
}




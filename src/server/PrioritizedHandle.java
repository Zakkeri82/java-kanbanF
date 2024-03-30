package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PrioritizedHandle implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandle(TaskManager taskManager) {
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
                    if (Pattern.matches("^/prioritized$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(httpExchange, response);
                        break;
                    } else {
                        System.out.println("Ожидается /prioritized запрос, а получили - " + path);
                        httpExchange.sendResponseHeaders(405, 0);
                        break;
                    }
                }
                default: {
                    System.out.println("Ожидается GET запрос, а получили - " + method);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();

        } finally {
            httpExchange.close();
        }
    }

    private void sendText(HttpExchange exchange, String response) throws IOException {
        byte[] resp = response.getBytes(UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        exchange.getResponseBody().write(resp);
    }
}

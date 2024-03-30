package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoriServerTest {
    TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = HttpTaskServer.getGson();
    private Task task;

    public HistoriServerTest() throws IOException {
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask5);
        taskManager.findEpic(epic);
        taskManager.findTask(task);
        taskManager.findSubtask(subtask5);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskType);

        assertNotNull(response, "История не возвращается");
        assertEquals(3, history.size(), "Не верное количество задач в истории");
    }
}

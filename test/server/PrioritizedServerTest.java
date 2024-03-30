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

public class PrioritizedServerTest {
    TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = HttpTaskServer.getGson();
    private Task task;

    public PrioritizedServerTest() throws IOException {
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
    void getPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T10:00", 30);
        Task task2 = new Task("Задача 2", "Завести задачу2", "2024-03-16T09:30", 15);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T08:00", 15);
        taskManager.createSubtasks(subtask5);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> prioritizedTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(response, "Список задач по приоритету не возвращается");
        assertEquals(3, prioritizedTasks.size(), "Не верное количество задач в списке");
        assertEquals(subtask5.getId(), prioritizedTasks.get(0).getId(), "Не верный приоритет первого элемента");
        assertEquals(task1.getId(), prioritizedTasks.get(2).getId(), "Не верный приоритет последнего элемента");
    }
}

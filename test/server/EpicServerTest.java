package server;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
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

public class EpicServerTest {
    TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = HttpTaskServer.getGson();
    private Task task;

    public EpicServerTest() throws IOException {
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
    void getEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);


        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(response.body(), taskType);

        assertNotNull(response, "Список эпиков не возвращается");
        assertEquals(1, epics.size(), "Не верное количество эпиков");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        final Epic taskFromManager = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());

        assertEquals(epicId, taskFromManager.getId(), "Не верный id эпика");
        assertEquals("Эпик 1", taskFromManager.getName(), "Некорректное имя эпика");

        uri = URI.create("http://localhost:8080/epics/10");
        request = HttpRequest.newBuilder().uri(uri).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void getEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        taskManager.createSubtasks(subtask);
        int epicId = epic.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(response, "Список сабтасок не возвращается");
        assertEquals(1, subtasks.size(), "Не верное количество сабтасок");
        assertEquals(subtask, subtasks.get(0), "Сабтаски не совпадают");

        uri = URI.create("http://localhost:8080/epics/10");
        request = HttpRequest.newBuilder().uri(uri).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void createEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> taskFromManager = taskManager.getEpics();

        assertNotNull(taskFromManager, "Эпики не возвращаются");
        assertEquals(1, taskFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик 1", taskFromManager.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic deleteEpic = taskManager.findEpic(epic);

        assertEquals(null, deleteEpic, "Эпик не удален");
    }
}

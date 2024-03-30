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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtasksServerTest {
    TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = HttpTaskServer.getGson();
    private Task task;

    public SubtasksServerTest() throws IOException {
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
    void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        taskManager.createSubtasks(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(response, "Список подзадач не возвращается");
        assertEquals(1, subtasks.size(), "Не верное количество подзадач");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        taskManager.createSubtasks(subtask);
        int subtaskId = subtask.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        final Subtask taskFromManager = gson.fromJson(response.body(), new TypeToken<Subtask>() {
        }.getType());

        assertEquals(subtaskId, taskFromManager.getId(), "Не верный id подзадачи");
        assertEquals("Подзадача1", taskFromManager.getName(), "Некорректное имя подзадачи");

        uri = URI.create("http://localhost:8080/subtasks/10");
        request = HttpRequest.newBuilder().uri(uri).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void createSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> taskFromManager = taskManager.getSubtasks();

        assertNotNull(taskFromManager, "Подзадачи не возвращаются");
        assertEquals(1, taskFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача1", taskFromManager.get(0).getName(), "Некорректное имя подзадачи");

        Subtask subtask1 = new Subtask("Подзадача2", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        taskJson = gson.toJson(subtask1);
        uri = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void updateSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        taskManager.createSubtasks(subtask);
        subtask.setName("Новая");
        String taskJson = gson.toJson(subtask);
        int taskId = subtask.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Subtask updateTask = taskManager.findSubtask(subtask);

        assertNotNull(updateTask, "Подзадача не возвращается");
        assertEquals("Новая", updateTask.getName(), "Некорректное имя подзадачи");

       Subtask subtask1 = new Subtask("Подзадача2", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);
        taskManager.createTask(subtask1);
        subtask.setStartTime(LocalDateTime.parse("2024-03-16T10:00"));
        taskJson = gson.toJson(subtask);
        uri = URI.create("http://localhost:8080/subtasks/" + taskId);
        request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void deleteSubask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T12:00", 15);
        taskManager.createSubtasks(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask deleteTask = taskManager.findSubtask(subtask);

        assertEquals(null, deleteTask, "Подадача не удалена");
    }
}

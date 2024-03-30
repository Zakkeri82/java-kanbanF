package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class TaskServerTest {
    TaskManager taskManager = Managers.getDefault();
    private HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    private final Gson gson = HttpTaskServer.getGson();
    private Task task;

    public TaskServerTest() throws IOException {
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
    void getTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertNotNull(response, "Список задач не возвращается");
        assertEquals(1, tasks.size(), "Не верное количество задач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        taskManager.createTask(task);
        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        final Task taskFromManager = gson.fromJson(response.body(), new TypeToken<Task>() {
        }.getType());

        assertEquals(taskId, taskFromManager.getId(), "Не верный id задачи");
        assertEquals("Задача 1", taskFromManager.getName(), "Некорректное имя задачи");

        uri = URI.create("http://localhost:8080/tasks/10");
        request = HttpRequest.newBuilder().uri(uri).GET().build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void createTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> taskFromManager = taskManager.getTasks();

        assertNotNull(taskFromManager, "Задачи не возвращаются");
        assertEquals(1, taskFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача 1", taskFromManager.get(0).getName(), "Некорректное имя задачи");

        Task task1 = new Task("Задача 2", "Завести задачу1", "2024-03-16T09:00", 60);
        taskJson = gson.toJson(task1);
        uri = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void updateTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        taskManager.createTask(task);
        task.setName("Новая");
        String taskJson = gson.toJson(task);
        int taskId = task.getId();

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task updateTask = taskManager.findTask(task);

        assertNotNull(updateTask, "Задача не возвращается");
        assertEquals("Новая", updateTask.getName(), "Некорректное имя задачи");

        Task task1 = new Task("Задача 2", "Завести задачу1", "2024-03-16T10:00", 60);
        taskManager.createTask(task1);
        task.setStartTime(LocalDateTime.parse("2024-03-16T10:00"));
        taskJson = gson.toJson(task);
        uri = URI.create("http://localhost:8080/tasks/" + taskId);
        request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task deleteTask = taskManager.findTask(task);

        assertEquals(null, deleteTask, "Задача не удалена");
    }
}

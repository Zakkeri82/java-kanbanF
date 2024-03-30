package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    public void BeforeEach() throws IOException {
        File file = File.createTempFile("test", "csv");
        taskManager = new FileBackedTaskManager(file);
    }
    @Test
    public void testException() {
        taskManager.file = new File("invalid", "tasks.csv");
        taskManager = new FileBackedTaskManager(taskManager.file);
        assertThrows(ManagerSaveException.class, () -> {
            Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
            taskManager.createTask(task);
        });
    }

    @Test
    public void loadFromFile() throws Exception {
        File file = File.createTempFile("test", "csv");
        Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createEpic(epic3);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask5);
        taskManager.findEpic(epic3);
        taskManager.findTask(task1);
        taskManager.findSubtask(subtask5);

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager.getTasks(), "Список задач пуст");
        assertNotNull(taskManager.getEpics(), "Список эпиков пуст");
        assertNotNull(taskManager.getSubtasks(), "Список сабтаск пуст");
        assertNotNull(taskManager.getHistory(), "История пуста");
    }
}
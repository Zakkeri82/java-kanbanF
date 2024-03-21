package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
            Task task = new Task("Задача 1", "Завести задачу1", "16.03.2024|09:00", 60);
            taskManager.createTask(task);
        });
    }
}
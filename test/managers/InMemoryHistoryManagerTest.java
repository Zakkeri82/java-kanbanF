package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;
    @BeforeEach
    public void BeforeEach() {
       taskManager = Managers.getDefault();
    }

    @Test
    public void addHistoryAllTypeTask() {
        Task task = new Task("Задача 1", "Завести задачу 1");
        Epic epic = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic);
        taskManager.createSubtasks(subtask);

        taskManager.findTask(task);
        taskManager.findEpic(epic);
        taskManager.findSubtask(subtask);

         taskManager.getHistory().size();
         assertEquals(3, taskManager.getHistory().size(), "История не записалась");
    }

    @Test
    public  void savedHistoryPreviousVersionTask() {
        Task task = new Task("Задача 1", "Завести задачу 1");
        Epic epic = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic);
        taskManager.createSubtasks(subtask);

        taskManager.findTask(task);
        taskManager.findEpic(epic);
        taskManager.findSubtask(subtask);
        task.setName("Задача 1 изменена");
        taskManager.findTask(task);
       String nameTaskIndex0 = taskManager.getHistory().get(0).getName();
       String nameTaskIndex3 = taskManager.getHistory().get(3).getName();

       assertNotEquals(nameTaskIndex0, nameTaskIndex3, "История задачи не сохранилась");


    }

    @Test
    public void savedHistoryOnly10Task() {
        Task task1 = new Task("Задача 1", "Завести задачу1");
        Task task2 = new Task("Задача 2", "Завести задачу2");
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        Epic epic4 = new Epic("Эпик 2", "Завести эпик2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3);
        Subtask subtask6 = new Subtask("Подзадача 2", "Для эпика 1", epic3);
        Subtask subtask7 = new Subtask("Подзадача 3", "Для эпика 1", epic3);
        taskManager.createSubtasks(subtask5);
        taskManager.createSubtasks(subtask6);
        taskManager.createSubtasks(subtask7);

        taskManager.findTask(task1);
        taskManager.findTask(task2);
        taskManager.findEpic(epic4);
        taskManager.findEpic(epic3);
        taskManager.findSubtask(subtask5);
        taskManager.findSubtask(subtask6);
        taskManager.findSubtask(subtask7);
        taskManager.findTask(task1);
        taskManager.findTask(task2);
        taskManager.findEpic(epic4);
        taskManager.findEpic(epic3);

        assertEquals(10, taskManager.getHistory().size(), "В историю записано не 10 задач");
    }
}
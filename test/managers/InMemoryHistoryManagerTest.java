package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryHistoryManagerTest {
    private static TaskManager taskManager;
    @BeforeEach
    public void BeforeEach() {
       taskManager = Managers.getDefault();
    }

    @Test
    public void addHistoryAllTypeTask() {
        Task task = new Task("Задача 1", "Завести задачу 1", "2024-03-17T10:00", 15);
        Epic epic = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", 2, "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask);

        taskManager.findTask(task);
        taskManager.findEpic(epic);
        taskManager.findSubtask(subtask);

         taskManager.getHistory().size();
         assertEquals(3, taskManager.getHistory().size(), "В историю записаны не все  задачи");
    }

    @Test
    public void nullHistory() {

        assertEquals(0, taskManager.getHistory().size(), "Ничего не искали, а история не пустая");
    }

    @Test
    public  void removeDuplicateTaskFromHistory() {
        Task task = new Task("Задача 1", "Завести задачу 1");
        taskManager.createTask(task);

        taskManager.findTask(task);
        taskManager.findTask(task);

       assertEquals(1, taskManager.getHistory().size(), "Дубль задачи не удален из истории просмотров");
    }

    @Test
    public void whenTaskRemoveThenRemoveTaskFromTheHistory () {
        Task task = new Task("Задача 1", "Завести задачу 1","2024-03-17T10:00", 15);
        Task task1 = new Task("Задача 2", "Завести задачу 2", "2024-03-17T11:00", 15);
        Epic epic = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createTask(task);
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask);
        taskManager.findTask(task);
        taskManager.findEpic(epic);
        taskManager.findSubtask(subtask);
        taskManager.findTask(task1);


        taskManager.removeSubtask(subtask);
        taskManager.removeTask(task);
        taskManager.removeTask(task1);


        assertFalse(taskManager.getHistory().contains(task) && taskManager.getHistory().contains(subtask) &&
                taskManager.getHistory().contains(task1), "Задачи не удалены из истории");
    }

    @Test
    void shouldAddNewTaskToTheTail() {
        Task task = new Task("Задача 1", "Завести задачу 1", "2024-03-16T10:00", 15);
        Epic epic = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", 2, "2024-03-17T10:00", 15);
        taskManager.createSubtasks(subtask);

        taskManager.findTask(task);
        taskManager.findEpic(epic);
        taskManager.findSubtask(subtask);

        assertEquals(taskManager.getHistory().get(2), subtask, "Последняя задача ндобавлена не в конец истории");
    }
}
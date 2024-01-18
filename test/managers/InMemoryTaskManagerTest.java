package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    public void BeforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void createTask() {
        Task task = new Task("Задача 1", "Завести задачу1");
        taskManager.createTask(task);

        final Task savedTask = taskManager.findTask(task);

        assertNotNull(savedTask, "Таска не найдена.");
        assertEquals(task, savedTask, "Таски не совпадают");

        final ArrayList<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createEpic() {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");

        taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.findEpic(epic);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final ArrayList<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createSubtask() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic);

        taskManager.createSubtasks(subtask);
        final  Subtask savedSubtask = taskManager.findSubtask(subtask);

        assertNotNull(subtask, "Сабтаска не найдена");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают");

        final ArrayList<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасок.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача 1", "Завести задачу1");

        taskManager.createTask(task);
        taskManager.findTask(task);

        final List<Task> history = taskManager.getHistory();
        // List<Task> history = historyManager.getHistory();
       //historyManager.add(task);

        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    public void tasksWithTheSameIdMustBeEqual() {
        Task task = new Task("Задача", "Завести задачу");
        Task task1 = new Task("Задача 1", "Завести задачу 1");
        task.setId(1);
        task1.setId(1);

        assertEquals(task, task1, "Таски не равны");
    }

    @Test
    public void epicsWithTheSameIdMustBeEqual() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        Epic epic1 = new Epic("Эпик 1", "Завести эпик 1");

        epic.setId(1);
        epic1.setId(1);

        assertEquals(epic, epic1, "Эпики не равны");
    }

    @Test
    public void subtasksWithTheSameIdMustBeEqual() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic);
        Subtask subtask1 = new Subtask("Подзадача2", "Для эпика 1", epic);

        subtask.setId(1);
        subtask1.setId(1);

        assertEquals(subtask, subtask1, "Сабтаски не равны");
    }

    @Test
    public void cannotAddSubtaskToNonExistentEpic(){
        Epic epic = new Epic("Эпик", "Завести эпик");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic);
        subtask.setEpicId(3);
        taskManager.createSubtasks(subtask);
        int sizeSubtaskArrayListAfterCreateSubtask = epic.getListSubtask().size();

       assertEquals(0, sizeSubtaskArrayListAfterCreateSubtask, "Добавлена сабтаска с несуществующим epicId");
    }

    @Test
    public void cannotUpdateEpicToNonExistentId(){
        Epic epic = new Epic("Эпик", "Завести эпик");
        epic.setId(10);

        taskManager.updateEpic(epic);

        assertNull(taskManager.findEpic(epic),"Обновлен эпик с несуществующим Id");
    }

    @Test
    public void cannotUpdateSubtaskToNonExistentId(){
        Epic epic = new Epic("Эпик", "Завести эпик");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic);
        subtask.setId(10);

        taskManager.updateSubtask(subtask);

        assertNull(taskManager.findSubtask(subtask), "Обновлена сабтаска с несуществующим Id");
        assertEquals(0, epic.getListSubtask().size());
    }

    @Test
    public void isNoConflictIdTask() {
        Task task = new Task("Задача", "Завести задачу");
       int taskIdBeforeCreate = 20;
       task.setId(taskIdBeforeCreate);

       taskManager.createTask(task);
       int taskIdAfterCreate = task.getId();

       assertNotEquals(taskIdBeforeCreate, taskIdAfterCreate, "Добавлена задача с заданным номером");
    }
}
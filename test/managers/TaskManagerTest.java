package managers;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @Test
    public void createTask() {
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
        taskManager.createTask(task);

        final Task savedTask = taskManager.findTask(task);

        assertNotNull(savedTask, "Таска не найдена.");
        assertEquals(task, savedTask, "Таски не совпадают");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void сreate_task_throws_ManagerSaveException() {
        Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T10:00", 30);
        Task task2 = new Task("Задача 2", "Завести задачу2", "2024-03-16T10:10", 15);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createEpic(epic3);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T10:15", 15);

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task2));
        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(subtask5));
    }

    @Test
    void update_task_throws_ManagerSaveException() {
        Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T10:00", 30);
        Task task2 = new Task("Задача 2", "Завести задачу2", "2024-03-16T11:00", 15);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T12:00", 15);
        taskManager.createSubtasks(subtask5);

        task2.setStartTime(LocalDateTime.parse("2024-03-16T10:00"));
        subtask5.setStartTime(LocalDateTime.parse("2024-03-16T10:00"));

        assertThrows(ManagerSaveException.class, () -> taskManager.updateTask(task2));
        assertThrows(ManagerSaveException.class, () -> taskManager.updateTask(subtask5));
    }

    @Test
    public void createEpic() {
        Epic epic = new Epic("Эпик 1", "Завести эпик1");

        taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.findEpic(epic);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void createSubtask() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);

        taskManager.createSubtasks(subtask);
        final Subtask savedSubtask = taskManager.findSubtask(subtask);

        assertNotNull(subtask, "Сабтаска не найдена");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество сабтасок.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Задача 1", "Завести задачу1", "2024-03-16T10:00", 15);

        taskManager.createTask(task);
        taskManager.findTask(task);

        final List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(1, history.size(), "История пустая.");
    }

    @Test
    public void tasksWithTheSameIdMustBeEqual() {
        Task task = new Task("Задача", "Завести задачу", "2024-03-16T10:00", 15);
        Task task1 = new Task("Задача 1", "Завести задачу 1", "2024-03-16T12:00", 15);
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
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);
        Subtask subtask1 = new Subtask("Подзадача2", "Для эпика 1", epic.getId(), "2024-03-16T11:00", 15);

        subtask.setId(1);
        subtask1.setId(1);

        assertEquals(subtask, subtask1, "Сабтаски не равны");
    }

    @Test
    public void cannotAddSubtaskToNonExistentEpic() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);
        subtask.setEpicId(3);
        taskManager.createSubtasks(subtask);
        int sizeSubtaskListAfterCreateSubtask = epic.getListSubtask().size();

        assertEquals(0, sizeSubtaskListAfterCreateSubtask, "Добавлена сабтаска с несуществующим epicId");
    }

    @Test
    public void cannotUpdateEpicToNonExistentId() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        epic.setId(10);

        taskManager.updateEpic(epic);

        assertNull(taskManager.findEpic(epic), "Обновлен эпик с несуществующим Id");
    }

    @Test
    public void cannotUpdateSubtaskToNonExistentId() {
        Epic epic = new Epic("Эпик", "Завести эпик");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic.getId(), "2024-03-16T10:00", 15);
        subtask.setId(10);

        taskManager.updateSubtask(subtask);

        assertNull(taskManager.findSubtask(subtask), "Обновлена сабтаска с несуществующим Id");
        assertEquals(0, epic.getListSubtask().size());
    }

    @Test
    public void isNoConflictIdTask() {
        Task task = new Task("Задача", "Завести задачу", "2024-03-16T10:00", 15);
        int taskIdBeforeCreate = 20;
        task.setId(taskIdBeforeCreate);

        taskManager.createTask(task);
        int taskIdAfterCreate = task.getId();

        assertNotEquals(taskIdBeforeCreate, taskIdAfterCreate, "Добавлена задача с заданным номером");
    }

    @Test
    void clearTasks() {
        Task task = new Task("Задача 1", "Завести задачу 1", "2024-03-16T10:00", 15);
        Task task1 = new Task("Задача 2", "Завести задачу 2", "2024-03-16T10:30", 15);
        taskManager.createTask(task);
        taskManager.createTask(task1);

        taskManager.clearTasks();

        assertEquals(0, taskManager.getTasks().size(), "Список задач не удален");
    }

    @Test
    void removeTask() {
        Task task = new Task("Задача 1", "Завести задачу 1", "2024-03-16T10:00", 15);
        taskManager.createTask(task);

        taskManager.removeTask(task);

        assertFalse(taskManager.getTasks().contains(task), "Задача не удалена");
    }

    @Test
    void clearEpics() {
        Epic epic1 = new Epic("Эпик 1", "Завести эпик 1");
        Epic epic2 = new Epic("Эпик 2", "Завести эпик 2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic1.getId(), "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask);

        taskManager.clearEpics();

        assertTrue(taskManager.getEpics().size() == 0, "Список эпиков не удален");
        assertTrue(taskManager.getSubtasks().size() == 0, "Подзадачи эпика не удалены");
    }

    @Test
    void removeEpic() {
        Epic epic1 = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic1.getId(), "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask);

        taskManager.removeEpic(epic1);

        assertFalse(taskManager.getEpics().contains(epic1), "Эпик не удален");
        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача эпика не удалена");
    }

    @Test
    void clearSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic1.getId(), "2024-03-16T10:00", 15);
        Subtask subtask1 = new Subtask("Подзадача2", "Для эпика 1", epic1.getId(), "2024-03-16T11:00", 15);
        taskManager.createSubtasks(subtask);
        taskManager.createSubtasks(subtask1);

        taskManager.clearSubtasks();

        assertTrue(taskManager.getSubtasks().size() == 0, "Список подзадач не удален");
        assertTrue(epic1.getListSubtask().size() == 0, "Подзадачи не удалены из эпика");
    }

    @Test
    void removeSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic1.getId(), "2024-03-16T10:00", 15);
        taskManager.createSubtasks(subtask);

        taskManager.removeSubtask(subtask);

        assertFalse(taskManager.getSubtasks().contains(subtask), "Подзадача не удалена из списка");
        assertFalse(epic1.getListSubtask().contains(subtask), "Подзадача не удалена из эпика");

    }

    @Test
    void updateEpicStatus() {
        Epic epic1 = new Epic("Эпик 1", "Завести эпик 1");
        taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Подзадача1", "Для эпика 1", epic1.getId(), "2024-03-16T10:00", 15);
        Subtask subtask1 = new Subtask("Подзадача2", "Для эпика 1", epic1.getId(), "2024-03-16T11:00", 15);

        taskManager.createSubtasks(subtask);
        taskManager.createSubtasks(subtask1);

        assertEquals(TaskStatus.NEW, epic1.getStatus(), "Статус эпика не NEW");

        subtask.setStatus(TaskStatus.DONE);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.DONE, epic1.getStatus(), "Статус эпика не DONE");

        subtask.setStatus(TaskStatus.NEW);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Статус эпика не IN_PROGRESS при NEW и DONE");

        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Статус эпика не IN_PROGRESS");
    }

    @Test
    public void prioritizedTasks() {
        Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T10:00", 30);
        Task task2 = new Task("Задача 2", "Завести задачу2", "2024-03-16T09:30", 15);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T08:00", 15);
        taskManager.createSubtasks(subtask5);

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(subtask5, prioritizedTasks.first(), "Не верный приоритет первого элемента");
        assertEquals(task1, prioritizedTasks.last(), "Не верный приоритет последнего элемента");

        /*List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(subtask5, prioritizedTasks.get(0), "Не верный приоритет первого элемента");
        assertEquals(task1, prioritizedTasks.get(2), "Не верный приоритет последнего элемента");*/
    }

    @Test
    public void getEndTime() {
        Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T10:00", 30);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createEpic(epic3);
        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T08:00", 15);
        taskManager.createSubtasks(subtask5);
        LocalDateTime taskEndTime = task1.getStartTime().plusMinutes(task1.getDuration().toMinutes());
        LocalDateTime epicEndTime = epic3.getStartTime().plusMinutes(epic3.getDuration().toMinutes());
        LocalDateTime subtaskEndTime = subtask5.getStartTime().plusMinutes(subtask5.getDuration().toMinutes());

        assertEquals(taskEndTime, task1.getEndTime(), "EndTime таски рассчитан не верно");
        assertEquals(epicEndTime, epic3.getEndTime(), "EndTime эпика рассчитан не верно");
        assertEquals(subtaskEndTime, subtask5.getEndTime(), "EndTime сабтаски рассчитан не верно");
    }
}
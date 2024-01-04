import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача 1", "Завести задачу1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Завести задачу2", TaskStatus.NEW);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1", TaskStatus.NEW);
        Epic epic4 = new Epic("Эпик 2", "Завести эпик 2",TaskStatus.NEW);


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", TaskStatus.NEW, epic3);
        Subtask subtask6 = new Subtask("Подзадача2", "Для эпика 1", TaskStatus.NEW, epic3);
        Subtask subtask7 = new Subtask("Подзадача1", "Для эпика 2", TaskStatus.NEW, epic4);

        taskManager.createSubtasks(subtask5);
        taskManager.createSubtasks(subtask6);
        taskManager.createSubtasks(subtask7);
        System.out.println(taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);

        task1.status = TaskStatus.IN_PROGRESS;
        subtask6.status = TaskStatus.DONE;
        subtask7.status = TaskStatus.IN_PROGRESS;
        epic3.name = "Переименовнный эпик  1";

        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtask6);
        taskManager.updateSubtask(subtask7);
        taskManager.updateEpic(epic3);

        System.out.println(taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(taskManager.subtasks);

        subtask5.status = TaskStatus.DONE;
        taskManager.updateSubtask(subtask5);

        taskManager.printSubtaskInEpic(epic3);
        taskManager.printSubtaskInEpic(epic4);

        taskManager.findTask(2);
        taskManager.findEpic(3);
        taskManager.findSubtask(1);

        taskManager.removeTask(task1);
        taskManager.removeEpic(epic3);
        taskManager.removeSubtask(subtask7);

        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
    }
}
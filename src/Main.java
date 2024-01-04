import managers.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача 1", "Завести задачу1");
        Task task2 = new Task("Задача 2", "Завести задачу2");
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        Epic epic4 = new Epic("Эпик 2", "Завести эпик 2");


        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);
        taskManager.createEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3);
        Subtask subtask6 = new Subtask("Подзадача2", "Для эпика 1", epic3);
        Subtask subtask7 = new Subtask("Подзадача1", "Для эпика 2", epic4);

        taskManager.createSubtasks(subtask5);
        taskManager.createSubtasks(subtask6);
        taskManager.createSubtasks(subtask7);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        subtask6.setStatus(TaskStatus.DONE);
        subtask7.setStatus(TaskStatus.IN_PROGRESS);
        epic3.setName("Переименовнный эпик  1");

        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtask6);
        taskManager.updateSubtask(subtask7);
        taskManager.updateEpic(epic3);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        subtask5.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask5);

        System.out.println(epic3.getListSubtask());
        System.out.println(epic4.getListSubtask());

        taskManager.findTask(task2);
        taskManager.findEpic(epic3);
        taskManager.findSubtask(subtask7);

        taskManager.removeTask(task1);
        taskManager.removeEpic(epic3);
        taskManager.removeSubtask(subtask7);

        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
    }
}
import managers.Managers;
import managers.TaskManager;
import tasks.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

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
        Subtask subtask8 = new Subtask("Подзадача 1", "Для эпика 2", epic4);
        Subtask subtask9 = new Subtask("Подзадача 2", "Для эпика 2", epic4);
        taskManager.createSubtasks(subtask5);
        taskManager.createSubtasks(subtask6);
        taskManager.createSubtasks(subtask7);
        taskManager.createSubtasks(subtask8);
        taskManager.createSubtasks(subtask9);

        taskManager.findTask(task2);
        taskManager.findTask(task2);
        /*taskManager.findEpic(epic4);
        taskManager.findEpic(epic3);
        taskManager.findTask(task1);
        taskManager.findSubtask(subtask5);
        taskManager.findSubtask(subtask6);
        taskManager.findSubtask(subtask7);*/

        System.out.println(taskManager.getHistory());

       /* taskManager.findSubtask(subtask7);
        taskManager.findTask(task2);
        taskManager.findTask(task1);
        taskManager.findSubtask(subtask7);
        taskManager.findEpic(epic4);
        taskManager.findEpic(epic3);

        System.out.println(taskManager.getHistory());

        taskManager.removeSubtask(subtask5);
        taskManager.removeTask(task1);

        System.out.println(taskManager.getHistory());

        taskManager.removeEpic(epic3);

        System.out.println(taskManager.getHistory());*/






    }

}
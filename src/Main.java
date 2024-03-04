import managers.Managers;
import managers.TaskManager;
import tasks.*;

public class Main {

    public static void main(String[] args) {
       TaskManager taskManager = Managers.getDefault();


        Task task1 = new Task("Задача 1", "Завести задачу1");
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createEpic(epic3);

        Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId());
        taskManager.createSubtasks(subtask5);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.findEpic(epic3);
        taskManager.findTask(task1);
        taskManager.findSubtask(subtask5);

        System.out.println(taskManager.getHistory());

        taskManager.findTask(task1);
        taskManager.findEpic(epic3);

        System.out.println(taskManager.getHistory());

    }
}
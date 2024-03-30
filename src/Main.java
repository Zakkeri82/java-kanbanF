import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

 public static void main(String[] args) {
  TaskManager taskManager = Managers.getDefault();

     Task task1 = new Task("Задача 1", "Завести задачу1", "2024-03-16T09:00", 60);
     Task task2 = new Task("Задача 2", "Завести задачу2", "2024-03-16T09:00", 30);
        Epic epic3 = new Epic("Эпик 1", "Завести эпик1");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic3);

     Subtask subtask5 = new Subtask("Подзадача1", "Для эпика 1", epic3.getId(), "2024-03-16T10:00", 15);
     Subtask subtask6 = new Subtask("Подзадача2", "Для эпика 1", epic3.getId(), "2024-03-17T10:16", 30);
     Subtask subtask7 = new Subtask("Подзадача3", "Для эпика 1", epic3.getId(), "2024-03-18T10:46", 30);
        taskManager.createSubtasks(subtask5);
        taskManager.createSubtasks(subtask6);
        taskManager.createSubtasks(subtask7);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getPrioritizedTasks());

        taskManager.findEpic(epic3);
        taskManager.findTask(task1);
        taskManager.findSubtask(subtask5);

        System.out.println(taskManager.getHistory());
    }
}
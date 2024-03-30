package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getHistory();

    void createTask(Task task);

    Task getTaskById(int id);

    Task findTask(Task task);

    void clearTasks();

    void removeTask(Task task);

    void updateTask(Task task);

    void createEpic(Epic epic);

    Epic getEpicById(int id);

    Epic findEpic(Epic epic);

    void clearEpics();

    void removeEpic(Epic epic);

    void updateEpic(Epic epic);

    void createSubtasks(Subtask subtask);

    Subtask getSubtaskById(int id);

    Subtask findSubtask(Subtask subtask);

    void clearSubtasks();

    void removeSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    TreeSet<Task> getPrioritizedTasks();
}


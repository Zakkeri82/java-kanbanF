package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Task> getHistory();

    int createTask(Task task);

    Task findTask(Task task);

    void clearTasks();

    void removeTask(Task task);

    void updateTask(Task task);

    int createEpic(Epic epic);

    Epic findEpic(Epic epic);

    void clearEpics();

    void removeEpic(Epic epic);

    void updateEpic(Epic epic);

    int createSubtasks(Subtask subtask);

    Subtask findSubtask(Subtask subtask);

    void clearSubtasks();

    void removeSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);


}


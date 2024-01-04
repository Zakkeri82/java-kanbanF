import java.util.Arrays;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int id = 0;

    public void createTask(Task task) {
        task.id = ++id;
        tasks.put(task.id, task);
    }

    public Task findTask(int id) {
            return tasks.get(id);
    }

    public void clearTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void removeTask(Task task) {
        tasks.remove(task.id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }


    public void createEpic(Epic epic) {
        epic.id = ++id;
        epic.status = TaskStatus.NEW;
        epics.put(epic.id, epic);
    }
    public Epic findEpic(int id) {
            return epics.get(id);
    }
    public void clearEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
        }
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    public void removeEpic(Epic epic) {
        epics.remove(epic.id);
        if (!epic.listSubtask.isEmpty()) {
            epic.listSubtask.clear();
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateEpicStatus(Epic epic) {
        if (!epic.listSubtask.isEmpty()) {
            int statusNew = 0;
            int statusDone = 0;
            for (Subtask subtask : epic.listSubtask) {

                switch (subtask.status) {
                    case NEW:
                        ++statusNew;
                        break;
                    case DONE:
                        ++statusDone;
                        break;
                    default:
                }
            }
            if (statusNew == epic.listSubtask.size() && epic.status != TaskStatus.NEW) {
                epic.status = TaskStatus.NEW;

            } else if (statusDone == epic.listSubtask.size() && epic.status != TaskStatus.DONE) {
                epic.status = TaskStatus.DONE;

            } else if (epic.status != TaskStatus.IN_PROGRESS) {
                epic.status = TaskStatus.IN_PROGRESS;
            }
        }
    }

    public void createSubtasks(Subtask subtask) {
        subtask.id = ++id;
        Epic epic = epics.get(subtask.idEpic);
        epic.listSubtask.add(subtask);
        subtasks.put(subtask.id, subtask);
        updateEpicStatus(epic);
    }
    public Subtask findSubtask(int id) {
        return subtasks.get(id);
    }
    public void printSubtaskInEpic(Epic epic) {
        if (!epic.listSubtask.isEmpty()) {
            System.out.println(epic.listSubtask);
        }
    }

    public void clearSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                epic.status = TaskStatus.NEW;
            }
        }
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.id);
        Epic epic = epics.get(subtask.idEpic);
        epic.listSubtask.remove(subtask);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.idEpic));
    }
}


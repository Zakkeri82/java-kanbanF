package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.HashMap;

public class TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

   protected int id = 0;

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public int createTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public Task findTask(Task task) {
        return tasks.get(task.getId());
    }

    public void clearTasks() {
            tasks.clear();
    }

    public void removeTask(Task task) {
        tasks.remove(task.getId());
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }


    public int createEpic(Epic epic) {
        epic.setId(++id);
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }
    public Epic findEpic(Epic epic) {
            return epics.get(epic.getId());
    }
    public void clearEpics() {
            epics.clear();
            if (!subtasks.isEmpty()) {
            subtasks.clear();
        }

    }

    public void removeEpic(Epic epic) {
        epics.remove(epic.getId());
            epic.getListSubtask().clear();
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateEpicStatus(Epic epic) {
        if (!epic.getListSubtask().isEmpty()) {
            int statusNew = 0;
            int statusDone = 0;
            for (Subtask subtask : epic.getListSubtask()) {

                switch (subtask.getStatus()) {
                    case NEW:
                        ++statusNew;
                        break;
                    case DONE:
                        ++statusDone;
                        break;
                    default:
                }
            }
            if (statusNew == epic.getListSubtask().size() && epic.getStatus() != TaskStatus.NEW) {
                epic.setStatus(TaskStatus.NEW);

            } else if (statusDone == epic.getListSubtask().size() && epic.getStatus() != TaskStatus.DONE) {
                epic.setStatus(TaskStatus.DONE);

            } else if (epic.getStatus() != TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public int createSubtasks(Subtask subtask) {
        subtask.setId(++id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getListSubtask().add(subtask);
        subtasks.put(subtask.getId(), subtask);
        return subtask.getId();

    }
    public Subtask findSubtask(Subtask subtask) {
        return subtasks.get(subtask.getId());
    }

    public void clearSubtasks() {
            subtasks.clear();
            for (Epic epic : epics.values()) {
                epic.setStatus(TaskStatus.NEW);
            }
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.getListSubtask().remove(subtask);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
    }
}


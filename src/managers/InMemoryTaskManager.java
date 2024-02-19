package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int id = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public int createTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Task findTask(Task task) {
        Task findTask = tasks.get(task.getId());
        if (findTask != null) {
            historyManager.add(task);
        }
        return findTask;
    }

    @Override
    public void clearTasks() {
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void removeTask(Task task) {
        tasks.remove(task.getId());
        historyManager.remove(task.getId());
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(++id);
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Epic findEpic(Epic epic) {
        Epic findEpic = epics.get(epic.getId());
        if (findEpic != null) {
            historyManager.add(epic);
        }
        return findEpic;
    }

    @Override
    public void clearEpics() {
        for (Integer key : epics.keySet()) {
            historyManager.remove(key);
        }
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeEpic(Epic epic) {
        historyManager.remove(epic.getId());
        epics.remove(epic.getId());
        epic.getListSubtask().clear();
        List<Subtask> listSubtask = getSubtasks();
        for (Subtask subtask : listSubtask) {
            if (subtask.getEpicId() == epic.getId()) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
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
            if (statusNew == epic.getListSubtask().size()) {
                epic.setStatus(TaskStatus.NEW);

            } else if (statusDone == epic.getListSubtask().size()) {
                epic.setStatus(TaskStatus.DONE);

            } else if (epic.getStatus() != TaskStatus.IN_PROGRESS) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public int createSubtasks(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            subtask.setId(++id);
            epic.getListSubtask().add(subtask);
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epic);
        }
        return subtask.getId();
    }

    @Override
    public Subtask findSubtask(Subtask subtask) {
        Subtask findSubtask = subtasks.get(subtask.getId());
        if (findSubtask != null) {
            historyManager.add(subtask);
        }
        return findSubtask;
    }

    @Override
    public void clearSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        historyManager.remove(subtask.getId());
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.getListSubtask().remove(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())
                && !epics.containsKey(subtask.getId()) && !subtasks.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }
}


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
    public ArrayList <Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList <Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList <Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public int createTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public  Task findTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            historyManager.add(task);
        }
        return tasks.get(task.getId());

    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void removeTask(Task task) {
        tasks.remove(task.getId());
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
        if (epics.containsKey(epic.getId())) {
            historyManager.add(epic);
        }
        return epics.get(epic.getId());
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeEpic(Epic epic) {
        epics.remove(epic.getId());
        epic.getListSubtask().clear();
        ArrayList<Subtask> listSubtask = getSubtasks();
        for (Subtask subtask : listSubtask) {
            if (subtask.getEpicId() == epic.getId()) {
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
        if (subtasks.containsKey(subtask.getId())) {
            historyManager.add(subtask);
        }
        return subtasks.get(subtask.getId());
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void removeSubtask(Subtask subtask) {
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


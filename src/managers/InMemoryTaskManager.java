package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final Comparator<Task> comparator = (task1, task2) -> {
        if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        }
        return task1.getStartTime().compareTo(task2.getStartTime());
    };
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);
    protected int id = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isIntersectsTasks(Task task) {
        if (getPrioritizedTasks().isEmpty()) {
            return false;
        } else if (task.getStartTime() != null) {
            return !getPrioritizedTasks().stream()
                    .filter(task1 -> task1.getEndTime() != null && !task.equals(task1))
                    .allMatch(task1 -> task.getStartTime().isAfter(task1.getEndTime())
                            || task.getStartTime().equals(task1.getEndTime())
                            || task.getEndTime().isBefore(task1.getStartTime())
                            || task.getEndTime().equals(task1.getStartTime()));
        }
        return true;
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        if (isIntersectsTasks(task)) {
           throw new ManagerSaveException("Задача не создана, т.к. пересекается с другой задачей");
        }
            task.setId(++id);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
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
        tasks.values()
                .forEach(task -> {
                    historyManager.remove(task.getId());
                    prioritizedTasks.remove(task);
                });
        tasks.clear();
    }

    @Override
    public void removeTask(Task task) {
        tasks.remove(task.getId());
        historyManager.remove(task.getId());
        prioritizedTasks.remove(task);
    }

    @Override
    public void updateTask(Task task) {
        if (isIntersectsTasks(task)) {
            throw new ManagerSaveException("Задача не обновлена, т.к. пересекается с другой задачей");
        }
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(++id);
        updateEpicStatus(epic);
        epics.put(epic.getId(), epic);
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
        epics.keySet()
                .forEach(historyManager::remove);
        subtasks.values()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeEpic(Epic epic) {
        historyManager.remove(epic.getId());
        epics.remove(epic.getId());
        epic.getListSubtask().clear();
        getSubtasks()
                .forEach(subtask -> {
                    if (subtask.getEpicId() == epic.getId()) {
                        historyManager.remove(subtask.getId());
                        subtasks.remove(subtask.getId());
                        prioritizedTasks.remove(subtask);
                    }
                });
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStartTime(epic);
            updateEpicDuration(epic);
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

    private void updateEpicStartTime(Epic epic) {
        if (!epic.getListSubtask().isEmpty()) {
            List<Task> sortListSubtask = epic.getListSubtask().stream()
                    .sorted(Comparator.comparing(Subtask::getStartTime))
                    .collect(Collectors.toList());
            epic.setStartTime(sortListSubtask.get(0).getStartTime());
        }
    }

    private void updateEpicDuration(Epic epic) {
        if (!epic.getListSubtask().isEmpty()) {
            List<Task> sortListSubtask = epic.getListSubtask().stream()
                    .sorted(Comparator.comparing(Subtask::getEndTime))
                    .collect(Collectors.toList());
            LocalDateTime epicEndTime = sortListSubtask.get(sortListSubtask.size() - 1).getEndTime();
            epic.setDuration(Duration.ofMinutes(Duration.between(epic.getStartTime(), epicEndTime).toMinutes()));
        }
    }

    @Override
    public void createSubtasks(Subtask subtask) {
        if (isIntersectsTasks(subtask)) {
            throw new ManagerSaveException("Сабтаска не создана, т.к. пересекается с другой задачей");
        }
            int epicId = subtask.getEpicId();
            if (epics.containsKey(epicId)) {
                Epic epic = epics.get(epicId);
                subtask.setId(++id);
                epic.getListSubtask().add(subtask);
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epic);
                updateEpicStartTime(epic);
                updateEpicDuration(epic);
                prioritizedTasks.add(subtask);
        }
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
        subtasks.values()
                .forEach(subtask -> {
                    historyManager.remove(subtask.getId());
                    prioritizedTasks.remove(subtask);
                });
        subtasks.clear();
        epics.values()
                .forEach(epic -> {
                    epic.getListSubtask().clear();
                    epic.setStatus(TaskStatus.NEW);
                    epic.setStartTime(null);
                    epic.setDuration(null);
                });
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        historyManager.remove(subtask.getId());
        subtasks.remove(subtask.getId());
        prioritizedTasks.remove(subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getListSubtask().remove(subtask);
        updateEpicStatus(epic);
        updateEpicStartTime(epic);
        updateEpicDuration(epic);
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (isIntersectsTasks(subtask)) {
            throw new ManagerSaveException("Сабтаска не обновлена, т.к. пересекается с другой задачей");
        }
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())
                && !epics.containsKey(subtask.getId()) && !subtasks.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }
}


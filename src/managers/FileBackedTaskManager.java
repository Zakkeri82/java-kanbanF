package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            if (!tasks.isEmpty()) {
                for (Task task : tasks.values()) {
                    bufferedWriter.write(CSVTaskFormatter.toString(task));
                }
            }
            if (!epics.isEmpty()) {
                for (Task task : epics.values()) {
                    bufferedWriter.write(CSVTaskFormatter.toString(task));
                }
            }
            if (!subtasks.isEmpty()) {
                for (Task task : subtasks.values()) {
                    bufferedWriter.write(CSVTaskFormatter.toString(task));
                }
            }
            fileWriter.write("\n");
            if (!getHistory().isEmpty()) {
                bufferedWriter.write(CSVTaskFormatter.historyToString(historyManager));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка записи файла");
        }
    }

   public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        CSVTaskFormatter taskFormatter1 = new CSVTaskFormatter();
        try (Reader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String value = bufferedReader.readLine();
                if (!value.isEmpty()) {
                    Task task = taskFormatter1.fromString(value);
                    if (task instanceof Epic) {
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) task);
                        int epicId = ((Subtask) task).getEpicId();
                        if (fileBackedTaskManager.epics.containsKey(epicId)) {
                            Epic epic = fileBackedTaskManager.epics.get(epicId);
                            epic.getListSubtask().add((Subtask) task);
                        }
                    } else {
                        fileBackedTaskManager.tasks.put(task.getId(), task);
                    }
                } else {
                    value = bufferedReader.readLine();
                    List<Integer> historiId = CSVTaskFormatter.historyFromString(value);
                    for (Integer id : historiId) {
                        if (fileBackedTaskManager.tasks.containsKey(id)) {
                            fileBackedTaskManager.historyManager.add(fileBackedTaskManager.tasks.get(id));
                        } else if (fileBackedTaskManager.epics.containsKey(id)) {
                            fileBackedTaskManager.historyManager.add(fileBackedTaskManager.epics.get(id));
                        } else if (fileBackedTaskManager.subtasks.containsKey(id)) {
                            fileBackedTaskManager.historyManager.add(fileBackedTaskManager.subtasks.get(id));
                        } else {
                            System.out.println("Id  не найден");
                        }
                    }
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return fileBackedTaskManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtasks(Subtask subtask) {
        super.createSubtasks(subtask);
        save();
    }

    @Override
    public Task findTask(Task task) {
        Task findTask = super.findTask(task);
        save();
        return findTask;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void removeTask(Task task) {
        super.removeTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Epic findEpic(Epic epic) {
        Epic findEpic = super.findEpic(epic);
        save();
        return findEpic;
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void removeEpic(Epic epic) {
        super.removeEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public Subtask findSubtask(Subtask subtask) {
        Subtask findSubtask = super.findSubtask(subtask);
        save();
        return findSubtask;
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void removeSubtask(Subtask subtask) {
        super.removeSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    public static void main(String[] args) {
        File file1 = new File("tasks.csv");
        FileBackedTaskManager backedTaskManager = loadFromFile(file1);
        System.out.println(backedTaskManager.getTasks() + "\n");
        System.out.println(backedTaskManager.getEpics() + "\n");
        System.out.println(backedTaskManager.getSubtasks() + "\n");
        System.out.println(backedTaskManager.getHistory());


    }
}

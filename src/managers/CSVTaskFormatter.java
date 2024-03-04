package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormatter {
    public String toString(Task task) {
        String type;
        String[] dataTask;
        if (task instanceof Epic) {
            type = TypeTask.EPIC.getUrl();
        } else if (task instanceof Subtask) {
            type = TypeTask.SUBTASK.getUrl();
        } else {
            type = TypeTask.TASK.getUrl();
        }
        if (task instanceof Subtask) {
            dataTask = new String[6];
            dataTask[0] = Integer.toString(task.getId());
            dataTask[1] = type;
            dataTask[2] = task.getName();
            dataTask[3] = task.getStatus().getUrl();
            dataTask[4] = task.getDescription();
            dataTask[5] = Integer.toString(((Subtask) task).getEpicId());
        } else {
            dataTask = new String[5];
            dataTask[0] = Integer.toString(task.getId());
            dataTask[1] = type;
            dataTask[2] = task.getName();
            dataTask[3] = task.getStatus().getUrl();
            dataTask[4] = task.getDescription();
        }
        return String.join(",", dataTask) + "\n";
    }

    public Task fromString(String value) {
        String[] dataTask = value.split(",");
        Task task = null;
        if (dataTask[1].equals("TASK")) {
            task = new Task(dataTask[2], dataTask[4]);
            task.setId(Integer.parseInt(dataTask[0]));
            task.setStatus(TaskStatus.valueOf(dataTask[3]));
        } else if (dataTask[1].equals("EPIC")) {
            task = new Epic(dataTask[2], dataTask[4]);
            task.setId(Integer.parseInt(dataTask[0]));
            task.setStatus(TaskStatus.valueOf(dataTask[3]));
        } else {
            task = new Subtask(dataTask[2], dataTask[4], Integer.parseInt(dataTask[5]));
            task.setId(Integer.parseInt(dataTask[0]));
            task.setStatus(TaskStatus.valueOf(dataTask[3]));
        }
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        String[] historyTaskId = new String[history.size()];
        for (int i = 0; i < historyTaskId.length; i++) {
            historyTaskId[i] = Integer.toString(history.get(i).getId());
        }
        return "\n" + String.join(",", historyTaskId) + "\n";
    }

    public static List<Integer> historyFromString(String value) {
        String[] historyTaskId = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (int i = 0; i < historyTaskId.length; i++) {
            history.add(Integer.parseInt(historyTaskId[i]));
        }
        return history;
    }
}

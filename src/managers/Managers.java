package managers;

import java.io.File;

public class Managers {
    public static final File FILE = new File("tasks.csv");

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(FILE);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

package managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void getDefaultNotNull() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Объект InMemoryTaskManager не создан");

    }

    @Test
    public void getDefaultHistoryNotNull() {

        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Объект InMemoryHistoryManager не создан");
    }
}
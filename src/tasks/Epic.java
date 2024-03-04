package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Subtask> listSubtask;

    public Epic(String name, String description) {
        super(name, description);
        listSubtask = new ArrayList<>();
    }

    public List<Subtask> getListSubtask() {
        return listSubtask;
    }
}

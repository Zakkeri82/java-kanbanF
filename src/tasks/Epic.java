package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Subtask> listSubtask;

    public ArrayList<Subtask> getListSubtask() {
        return listSubtask;
    }

    public Epic(String name, String description ){
        super(name, description);
        listSubtask = new ArrayList<>();
    }
}

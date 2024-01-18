package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Subtask> listSubtask;

    public Epic(String name, String description ){
        super(name, description);
        listSubtask = new ArrayList<>();
    }

    public ArrayList<Subtask> getListSubtask() {
        return listSubtask;
    }
}

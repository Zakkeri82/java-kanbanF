import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> listSubtask;
    public Epic(String name, String detscription, TaskStatus status ){
        super(name, detscription, status);
        listSubtask = new ArrayList<>();
    }
}

public class Subtask extends Task {
    int idEpic;

    public Subtask(String name, String detscription, TaskStatus status, Epic epic) {
        super(name, detscription, status);
        idEpic = epic.id;
    }
}

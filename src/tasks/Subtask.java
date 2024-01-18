package tasks;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String detscription, Epic epic) {
        super(name, detscription);
        epicId = epic.id;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}

package tasks;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String detscription, int epicId) {
        super(name, detscription);
        this.epicId  = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}

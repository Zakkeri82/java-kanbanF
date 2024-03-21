package tasks;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(String name, String description, int epicId, String startTime, int duration) {
        super(name, description, startTime, duration);
        this.epicId  = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}


public class Task {
    public String name;
    public String detscription;
    public TaskStatus status;
    int id;

    public Task(String name, String detscription, TaskStatus status) {
        this.name = name;
        this.detscription = detscription;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Номер: " + id + ", имя: " + name + ", описание: " + detscription + ", статус: " + status + "\n";
    }
}

enum TaskStatus { NEW, IN_PROGRESS, DONE}


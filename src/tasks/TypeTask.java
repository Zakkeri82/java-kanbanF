package tasks;

public enum TypeTask {
    TASK("TASK"),
    EPIC("EPIC"),
    SUBTASK("SUBTASK");

    private final String url;

    TypeTask(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

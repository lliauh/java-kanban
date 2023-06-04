package task;

public class Subtask extends Task {
    public Subtask(String title, String description) {
        super(title, description);
        this.type = Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status +
                ", epicId = " + epicId + "}";
    }
}

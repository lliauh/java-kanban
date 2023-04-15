package taskmanager;

public class Subtask extends Task {
    Integer epicId;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "taskmanager.Subtask{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status +
                ", epicId = " + epicId + "}";
    }
}

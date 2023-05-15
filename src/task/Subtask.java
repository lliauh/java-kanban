package task;

public class Subtask extends Task {
    protected Integer epicId;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
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

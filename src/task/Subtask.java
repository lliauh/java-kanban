package task;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    public Subtask(String title, String description) {
        super(title, description);
        this.type = Type.SUBTASK;
    }

    public Subtask(String title, String description, LocalDateTime startTime, Integer duration) {
        super(title, description, startTime, duration);
        this.type = Type.SUBTASK;
    }

    @Override
    public String toString() {
        return "task.Subtask{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status +
                ", type = " + type + '\'' +
                ", startTime = " + startTime + '\'' +
                ", duration = " + duration + '\'' +
                ", epicId = " + epicId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(title, subtask.title) && Objects.equals(description, subtask.description) &&
                Objects.equals(id, subtask.id) && Objects.equals(status, subtask.status) &&
                Objects.equals(type, subtask.type) && Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (title != null) {
            hash = 31 * (hash + title.hashCode());
        }

        if (description != null) {
            hash = 31 * (hash + description.hashCode());
        }

        if (id != null) {
            hash = 31 * (hash + id.hashCode());
        }

        if (status != null) {
            hash = 31 * (hash + status.hashCode());
        }

        if (type != null) {
            hash = 31 * (hash + type.hashCode());
        }

        if (epicId != null) {
            hash = 31 * (hash + epicId.hashCode());
        }

        return hash;
    }
}

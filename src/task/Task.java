package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected Integer id;
    protected Status status;
    protected Integer epicId;
    protected Type type;
    protected Integer duration;
    protected LocalDateTime startTime;


    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.type = Type.TASK;
    }

    public Task(String title, String description, LocalDateTime startTime, Integer duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.status = Status.NEW;
        this.type = Type.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public Type getType() {
        return type;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(Duration.ofMinutes(duration));
        } else {
            return null;
        }
    }

    public Integer getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "task.Task{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) &&
                Objects.equals(id, task.id) && Objects.equals(status, task.status) && Objects.equals(type, task.type);
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

        return hash;
    }
}

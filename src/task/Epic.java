package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.type = Type.EPIC;
    }

    public Epic(String title, String description, LocalDateTime startTime, Integer duration, LocalDateTime endTime) {
        super(title, description, startTime, duration);
        this.endTime = endTime;
        this.type = Type.EPIC;
    }

    public void addSubtask(Subtask subtask) {
        if (!subtasks.contains(subtask.getId())) {;
            subtasks.add(subtask.getId());

            if (subtask.getDuration() != null) {
                if (duration == null) {
                    duration = subtask.getDuration();
                } else {
                    duration = duration + subtask.getDuration();
                }
            }

            if (subtask.getStartTime() != null) {
                if (startTime == null) {
                    startTime = subtask.getStartTime();
                } else {
                    if (subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                }
            }

            if (subtask.getEndTime() != null) {
                if (endTime == null) {
                    endTime = subtask.getEndTime();
                } else {
                    if (subtask.getEndTime().isAfter(endTime)) {
                        endTime = subtask.getEndTime();
                    }
                }
            }
        }
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status +
                ", type = " + type + '\'' +
                ", subtasksId = " + subtasks + '\'' +
                ", startTime = " + startTime + '\'' +
                ", endTime = " + startTime + '\'' +
                ", duration = " + duration + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(title, epic.title) && Objects.equals(description, epic.description) &&
                Objects.equals(id, epic.id) && Objects.equals(status, epic.status) && Objects.equals(type, epic.type) &&
                Objects.equals(subtasks, epic.subtasks);
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

        if (subtasks != null) {
            hash = 31 * (hash + subtasks.hashCode());
        }

        return hash;
    }
}

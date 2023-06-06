package task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    protected List<Integer> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
        this.type = Type.EPIC;
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "task.Epic{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status +
                ", subtasksId = " + subtasks + "}";
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

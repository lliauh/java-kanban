package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    protected List<Integer> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
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
}

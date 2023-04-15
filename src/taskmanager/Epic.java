package taskmanager;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addSubtask(int subtaskId) {
        subtasks.add(subtaskId);
    }

    @Override
    public String toString() {
        return "taskmanager.Epic{" +
                "id = " + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status = " + status +
                ", subtasksId = " + subtasks + "}";
    }
}

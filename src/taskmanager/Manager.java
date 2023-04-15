package taskmanager;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public int createSubtask(Subtask subtask, Integer epicId) {
        subtask.setId(nextId);
        nextId++;
        subtask.setEpicId(epicId);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).addSubtask(subtask.getId());
        return subtask.getId();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        boolean subtasksIsEmpty = epic.subtasks.isEmpty();
        boolean allSubtasksAreNew = false;
        boolean allSubtasksAreDone = false;
        if (!subtasksIsEmpty) {
            allSubtasksAreNew = true;
            allSubtasksAreDone = true;
            for (Integer subtaskId : epic.subtasks) {
                if (subtasks.get(subtaskId).status != Status.NEW) {
                    allSubtasksAreNew = false;
                }
                if (subtasks.get(subtaskId).status != Status.DONE) {
                    allSubtasksAreDone = false;
                }
            }
        }

        if (subtasksIsEmpty || allSubtasksAreNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksAreDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }

        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask.epicId != null) {
            boolean allSubtasksAreNew = true;
            boolean allSubtasksAreDone = true;
            Epic epic = epics.get(subtask.epicId);
            for (Integer subtaskId : epic.subtasks) {
                if (subtasks.get(subtaskId).status != Status.NEW) {
                    allSubtasksAreNew = false;
                }
                if (subtasks.get(subtaskId).status != Status.DONE) {
                    allSubtasksAreDone = false;
                }
            }

            if (allSubtasksAreNew) {
                epic.setStatus(Status.NEW);
            } else if (allSubtasksAreDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }

        subtasks.put(subtask.getId(), subtask);
    }

    public HashMap getAllTasks() {
        return tasks;
    }

    public HashMap getAllEpics() {
        return epics;
    }

    public HashMap getAllSubtasks() {
        return subtasks;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            subtask.epicId = null;
        }
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
        }
    }

    public Task getTaskById(Integer taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(Integer epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtaskById(Integer subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void removeTaskById(Integer taskId) {
        tasks.remove(taskId);
    }

    public void removeSubtaskById(Integer subtaskId) {
        subtasks.remove(subtaskId);
        for (Epic epic : epics.values()) {
            if (epic.subtasks.contains(subtaskId)) {
                epic.subtasks.remove(subtaskId);
            }
        }
    }

    public void removeEpicById(Integer epicId) {
        epics.remove(epicId);
        for (Subtask subtask : subtasks.values()) {
            if (subtask.epicId == epicId) {
                subtask.epicId = null;
            }
        }
    }

    public ArrayList<Subtask> getSubTasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        if (!epic.subtasks.isEmpty()) {
            for (Integer subtaskId : epic.subtasks) {
                subtasksList.add(subtasks.get(subtaskId));
            }
        }
        return subtasksList;
    }
}

package taskmanager;

import task.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    public int nextId = 1;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public int createTask(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubtask(Subtask subtask, Integer epicId) {
        subtask.setId(nextId);
        nextId++;
        subtask.setEpicId(epicId);
        subtasks.put(subtask.getId(), subtask);
        epics.get(epicId).addSubtask(subtask.getId());
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        boolean subtasksIsEmpty = epic.getSubtasks().isEmpty();
        boolean allSubtasksAreNew = false;
        boolean allSubtasksAreDone = false;
        if (!subtasksIsEmpty) {
            allSubtasksAreNew = true;
            allSubtasksAreDone = true;
            for (Integer subtaskId : epic.getSubtasks()) {
                if (subtasks.get(subtaskId).getStatus() != Status.NEW) {
                    allSubtasksAreNew = false;
                }
                if (subtasks.get(subtaskId).getStatus() != Status.DONE) {
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

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getEpicId() != null) {
            boolean allSubtasksAreNew = true;
            boolean allSubtasksAreDone = true;
            Epic epic = epics.get(subtask.getEpicId());
            for (Integer subtaskId : epic.getSubtasks()) {
                if (subtasks.get(subtaskId).getStatus() != Status.NEW) {
                    allSubtasksAreNew = false;
                }
                if (subtasks.get(subtaskId).getStatus() != Status.DONE) {
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

    @Override
    public List getAllTasks() {
        return List.of(tasks);
    }

    @Override
    public List getAllEpics() {
        return List.of(epics);
    }

    @Override
    public List getAllSubtasks() {
        return List.of(subtasks);
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
    }

    @Override
    public Task getTaskById(Integer taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
            return epics.get(epicId);
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            historyManager.add(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        } else {
            return null;
        }
    }

    @Override
    public void removeTaskById(Integer taskId) {
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeSubtaskById(Integer subtaskId) {
        Integer epicId = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(epicId);
        subtasks.remove(subtaskId);
        epic.getSubtasks().remove(subtaskId);
        historyManager.remove(subtaskId);

        boolean subtasksIsEmpty = epic.getSubtasks().isEmpty();
        boolean allSubtasksAreNew = false;
        boolean allSubtasksAreDone = false;
        if (!subtasksIsEmpty) {
            allSubtasksAreNew = true;
            allSubtasksAreDone = true;
            for (Integer epicSubtaskId : epic.getSubtasks()) {
                if (subtasks.get(epicSubtaskId).getStatus() != Status.NEW) {
                    allSubtasksAreNew = false;
                }
                if (subtasks.get(epicSubtaskId).getStatus() != Status.DONE) {
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
    }

    @Override
    public void removeEpicById(Integer epicId) {
        for (Integer subtaskId : epics.get(epicId).getSubtasks()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public List<Subtask> getSubTasksByEpic(Epic epic) {
        List<Subtask> subtasksList = new ArrayList<>();
        if (!epic.getSubtasks().isEmpty()) {
            for (Integer subtaskId : epic.getSubtasks()) {
                subtasksList.add(subtasks.get(subtaskId));
            }
        }
        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public int getNextId() {
        return nextId;
    }
}
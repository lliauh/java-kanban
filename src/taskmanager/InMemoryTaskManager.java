package taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

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

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.epicId != null) {
            boolean allSubtasksAreNew = true;
            boolean allSubtasksAreDone = true;
            Epic epic = epics.get(subtask.getEpicId());
            for (Integer subtaskId : epic.getSubtasks()) {
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
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        historyManager.add(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
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

        boolean subtasksIsEmpty = epic.subtasks.isEmpty();
        boolean allSubtasksAreNew = false;
        boolean allSubtasksAreDone = false;
        if (!subtasksIsEmpty) {
            allSubtasksAreNew = true;
            allSubtasksAreDone = true;
            for (Integer epicSubtaskId : epic.getSubtasks()) {
                if (subtasks.get(epicSubtaskId).status != Status.NEW) {
                    allSubtasksAreNew = false;
                }
                if (subtasks.get(epicSubtaskId).status != Status.DONE) {
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
    public ArrayList<Subtask> getSubTasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        if (!epic.subtasks.isEmpty()) {
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
}
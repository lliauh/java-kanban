package taskmanager;

import exceptions.TimeIntersectionException;
import task.*;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    public int nextId = 1;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    @Override
    public Integer createTask(Task task) {
        try {
            task.setId(nextId);
            tasksTimeIntersectionValidate(task);
            nextId++;
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            return task.getId();
        } catch (TimeIntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    @Override
    public Integer createEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask, Integer epicId) {
        try {
            subtask.setId(nextId);
            tasksTimeIntersectionValidate(subtask);
            nextId++;
            subtask.setEpicId(epicId);
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicId).addSubtask(subtask);
            prioritizedTasks.add(subtask);
            return subtask.getId();
        } catch (TimeIntersectionException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        try {
            tasksTimeIntersectionValidate(task);
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }   catch (TimeIntersectionException exception) {
            System.out.println(exception.getMessage());
        }
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
        try {
            tasksTimeIntersectionValidate(subtask);
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);

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
        } catch (TimeIntersectionException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void tasksTimeIntersectionValidate(Task newTask) throws TimeIntersectionException {
        if (newTask.getStartTime() == null) {
            return;
        }

        for (final Task task : prioritizedTasks) {
            if (task.getStartTime() == null || task.getEndTime() == null) {
                return;
            } else if (!task.equals(newTask) && !newTask.getStartTime().isAfter(task.getEndTime()) &&
                    !newTask.getEndTime().isBefore(task.getStartTime())) {
                throw new TimeIntersectionException("Задачи пересекаются по времени выполнения!");
            }
        }
    }
}
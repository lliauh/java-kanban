package taskmanager;

import task.*;
import java.util.List;
import java.util.ArrayList;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(Subtask subtask, Integer epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List getAllTasks();

    List getAllEpics();

    List getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(Integer taskId);

    Epic getEpicById(Integer epicId);

    Subtask getSubtaskById(Integer subtaskId);

    void removeTaskById(Integer taskId);

    void removeSubtaskById(Integer subtaskId);

    void removeEpicById(Integer epicId);

    List<Subtask> getSubTasksByEpic(Epic epic);

    List<Task> getHistory();
}

package taskmanager;

import java.util.List;
import java.util.ArrayList;

public interface TaskManager {

    public int createTask(Task task);

    public int createEpic(Epic epic);

    public int createSubtask(Subtask subtask, Integer epicId);

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubtask(Subtask subtask);

    public List getAllTasks();

    public List getAllEpics();

    public List getAllSubtasks();

    public void removeAllTasks();

    public void removeAllEpics();

    public void removeAllSubtasks();

    public Task getTaskById(Integer taskId);

    public Epic getEpicById(Integer epicId);

    public Subtask getSubtaskById(Integer subtaskId);

    public void removeTaskById(Integer taskId);

    public void removeSubtaskById(Integer subtaskId);

    public void removeEpicById(Integer epicId);

    public ArrayList<Subtask> getSubTasksByEpic(Epic epic);
}

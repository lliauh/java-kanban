package taskmanager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path fileName;

    public FileBackedTasksManager(String fileName) {
        this.fileName = Paths.get(fileName);
    }

    @Override
    public Integer createTask(Task task) {
        Integer taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public Integer createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Integer createSubtask(Subtask subtask, Integer epicId) {
        int subtaskId = super.createSubtask(subtask, epicId);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(Integer taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();
        return subtask;
    }

    @Override
    public void removeTaskById(Integer taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubtaskById(Integer subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeEpicById(Integer epicId) {
        super.removeEpicById(epicId);
        save();
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName.toFile(), StandardCharsets.UTF_8))) {
            fileWriter.write("id,type,name,status,description,epic,startTime,endTime,duration\n");

            for (Task task : tasks.values()) {
                fileWriter.write(Formatter.toString(task) + "\n");
            }

            for (Epic epic : epics.values()) {
                fileWriter.write(Formatter.toString(epic) + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(Formatter.toString(subtask) + "\n");
            }

            fileWriter.write("\n");
            fileWriter.write(Formatter.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении файла.");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerLoadException {
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getAbsolutePath());

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int lineNumber = 1;
            Map<Integer, String> lines = new HashMap<>();
            while (fileReader.ready()) {
                lines.put(lineNumber, fileReader.readLine());
                lineNumber++;
            }

            int maxId = 0;
            for (int i = 2; i < lines.size() - 1; i++) {
                Task task = Formatter.fromString(lines.get(i));

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task.getType() == Type.TASK) {
                    manager.updateTask(task);
                } else if (task.getType() == Type.EPIC) {
                    manager.updateEpic((Epic) task);
                } else if (task.getType() == Type.SUBTASK) {
                    manager.updateSubtask((Subtask) task);
                    manager.epics.get(task.getEpicId()).addSubtask((Subtask) task);
                }
            }

            List<Integer> history = Formatter.historyFromString(lines.get(lines.size()));
            for (Integer id : history) {
                if (manager.getTaskById(id) != null) {
                    continue;
                } else if (manager.getEpicById(id) != null) {
                    continue;
                } else {
                    manager.getSubtaskById(id);
                }
            }

            manager.nextId = maxId + 1;
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при загрузке файла.");
        }

        return manager;
    }
}

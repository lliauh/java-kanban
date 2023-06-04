package taskmanager;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path fileName;

    public FileBackedTasksManager(String fileName) {
        this.fileName = Paths.get(fileName);
        save();
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int createSubtask(Subtask subtask, Integer epicId) {
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

    private String toString(Task task) {
        if (task.getType() == Type.SUBTASK) {
            return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getEpicId();
        } else {
            return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + ",";
        }
    }

    private static Task fromString(String value) {
        String[] split = value.split(",");
        switch (split[1]) {
            case "TASK":
                Task task = new Task(split[2], split[4]);
                task.setId(Integer.parseInt(split[0]));
                task.setStatus(extractStatusFromString(split[3]));
                return task;
            case "EPIC":
                Epic epic = new Epic(split[2], split[4]);
                epic.setId(Integer.parseInt(split[0]));
                epic.setStatus(extractStatusFromString(split[3]));
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(split[2], split[4]);
                subtask.setId(Integer.parseInt(split[0]));
                subtask.setStatus(extractStatusFromString(split[3]));
                subtask.setEpicId(Integer.parseInt(split[5]));
                return subtask;
        }

        return null;
    }

    private static Status extractStatusFromString(String status) {
        if (status.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else if (status.equals("DONE")) {
            return Status.DONE;
        } else {
            return Status.NEW;
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> historyList = manager.getHistory();
        String[] historyIds = new String[historyList.size()];

        int counter = 0;
        for (Task task : historyList) {
            historyIds[counter] = Integer.toString(task.getId());
            counter++;
        }

        return String.join(",", historyIds);
    }

    private static List<Integer> historyFromString(String value) {
        String[] splitted = value.split(",");
        Integer[] splittedInt = new Integer[splitted.length];

        for (int i = 0; i < splitted.length; i++) {
            splittedInt[i] = Integer.valueOf(splitted[i]);
        }

        return Arrays.asList(splittedInt);
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName.toFile(), StandardCharsets.UTF_8))) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Task task : tasks.values()) {
                fileWriter.write(toString(task) + "\n");
            }

            for (Epic epic : epics.values()) {
                fileWriter.write(toString(epic) + "\n");
            }

            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(toString(subtask) + "\n");
            }

            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при сохранении файла.");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerLoadException {
        FileBackedTasksManager manager = new FileBackedTasksManager(file.getName());

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            int lineNumber = 1;
            Map<Integer, String> lines = new HashMap<>();
            while (fileReader.ready()) {
                lines.put(lineNumber, fileReader.readLine());
                lineNumber++;
            }

            int maxId = 0;
            for (int i = 2; i < lines.size() - 1; i++) {
                Task task = fromString(lines.get(i));

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task.getType() == Type.TASK) {
                    manager.updateTask(task);
                } else if (task.getType() == Type.EPIC) {
                    manager.updateEpic((Epic) task);
                } else if (task.getType() == Type.SUBTASK) {
                    manager.updateSubtask((Subtask) task);
                    manager.epics.get(task.getEpicId()).addSubtask(task.getId());
                }
            }

            List<Integer> history = historyFromString(lines.get(lines.size()));
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

    public static void main(String[] args) {
        System.out.println("Поехали! Проверяем первый менеджер и выгрузку в файл.");
        TaskManager manager = new FileBackedTasksManager("src/backup.csv");

        Task task1 = new Task("Таск1: первое название", "Таск1: первое описание");
        Task task2 = new Task("Таск2: второе название", "Таск2: второе описание");

        Epic epic1 = new Epic("Эпик1", "Описание Эпика1");
        Epic epic2 = new Epic("Эпик2", "Описание Эпика2");

        Subtask subtask1 = new Subtask("Сабтаск1", "Описание сабтаска1");
        Subtask subtask2 = new Subtask("Сабтаск2", "Описание сабтаска2");
        Subtask subtask3 = new Subtask("Сабтаск3", "Описание сабтаска3");


        manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic1.getId());

        System.out.println("Проверяем вывод всех задач и истории первого менеджера:");
        System.out.println("");
        System.out.println("");

        manager.getTaskById(1);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getTaskById(2);
        manager.getEpicById(4);
        manager.getEpicById(3);
        manager.getEpicById(3);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getSubtaskById(7);
        manager.getEpicById(4);
        manager.removeTaskById(2);

        System.out.println(manager.getHistory());
        System.out.println(manager.getAllTasks());

        System.out.println("В файл выгрузились, проверяем второй менеджер и загрузку из файла.");
        System.out.println("");
        System.out.println("");

        TaskManager managerFromFile = loadFromFile(new File("src/backup.csv"));

        System.out.println(managerFromFile.getHistory());
        System.out.println(managerFromFile.getAllTasks());

        System.out.println("");
        System.out.println("");
        System.out.println("Проверим следующий ID задачи у первого менеджера: " + manager.getNextId());
        System.out.println("Сравним его со следующим ID задачи у второго менеджера: " + managerFromFile.getNextId());



    }
}

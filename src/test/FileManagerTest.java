package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Subtask;
import task.Task;
import task.Epic;
import taskmanager.FileBackedTasksManager;
import taskmanager.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.time.LocalDateTime;

public class FileManagerTest extends TaskManagerTest {

    @BeforeEach
    public void createManager() {
        taskManager = new FileBackedTasksManager("output/test_backup.csv");
    }

    @Test
    public void saveAndLoadTestEmptyTasksAndHistory() {
        TaskManager manager = new FileBackedTasksManager("output/test_load_save_empty.csv");
        Task task1 = new Task("Таск1: первое название", "Таск1: первое описание");
        manager.createTask(task1);
        manager.removeAllTasks();

        TaskManager managerFromFile = FileBackedTasksManager.loadFromFile(new
                File("output/test_load_save_empty.csv"));

        assertTrue(managerFromFile.getAllTasks().isEmpty(), "Проблема в загрузке пустого файла.");
        assertTrue(managerFromFile.getHistory().isEmpty(), "Проблема в загрузке пустого файла.");
    }

    @Test
    public void saveAndLoadEpicWithoutSubtasks() {
        TaskManager manager = new FileBackedTasksManager("output/test_load_save_epic.csv");
        Epic epic1 = new Epic("Epic1: первое название", "Epic1: первое описание");
        manager.createEpic(epic1);
        manager.getEpicById(epic1.getId());

        TaskManager managerFromFile = FileBackedTasksManager.loadFromFile(new
                File("output/test_load_save_epic.csv"));

        assertEquals(manager.getAllEpics(), managerFromFile.getAllEpics(), "Проблема в загрузке эпика " +
                "без подзадач");
    }

    @Test
    public void saveAndLoadTest() {
        TaskManager manager = new FileBackedTasksManager("output/test_load_save.csv");
        Task task1 = new Task("Task1: название", "Task1: описание",
                LocalDateTime.parse("2023-07-30T00:21:21"), 20);
        Task task2 = new Task("Task2: название", "Task2: описание",
                LocalDateTime.parse("2023-07-31T00:21:21"), 15);
        Epic epic1 = new Epic("Epic1: название", "Epic1: описание");
        Epic epic2 = new Epic("Epic2: название", "Epic2: описание");
        Subtask subtask1 = new Subtask("Subtask1: название", "Subtask1: описание",
                LocalDateTime.parse("2023-07-30T05:21:21"), 10);
        Subtask subtask2 = new Subtask("Subtask2: название", "Subtask2: описание",
                LocalDateTime.parse("2023-07-30T08:21:21"), 20);
        Subtask subtask3 = new Subtask("Subtask3: название", "Subtask3: описание",
                LocalDateTime.parse("2023-07-30T12:21:21"), 5);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubtask(subtask1, epic2.getId());
        manager.createSubtask(subtask2, epic2.getId());
        manager.createSubtask(subtask3, epic1.getId());
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.DONE);
        manager.getSubtaskById(subtask2.getId());
        manager.getEpicById(epic1.getId());

        TaskManager managerFromFile = FileBackedTasksManager.loadFromFile(new
                File("output/test_load_save.csv"));

        assertEquals(manager.getAllTasks(), managerFromFile.getAllTasks(), "Проблема в загрузке из файла.");
        assertEquals(manager.getAllSubtasks(), managerFromFile.getAllSubtasks(), "Проблема в " +
                "загрузке из файла.");
        assertEquals(manager.getAllEpics(), managerFromFile.getAllEpics(), "Проблема в загрузке из файла.");
        assertEquals(manager.getHistory(), managerFromFile.getHistory(), "Проблема в загрузке из файла.");
    }

}

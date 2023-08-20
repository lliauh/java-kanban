import taskmanager.*;
import task.*;
import httpservice.*;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();

        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078/");

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
        manager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);
        manager.getSubtaskById(subtask2.getId());
        manager.getEpicById(epic1.getId());

        HttpTaskManager managerFromServer = HttpTaskManager.loadFromServer("http://localhost:8078/");
        System.out.println(managerFromServer.getAllTasks());
        System.out.println(managerFromServer.getAllEpics());
        System.out.println(managerFromServer.getAllSubtasks());
        System.out.println(managerFromServer.getHistory());
        System.out.println(managerFromServer.getNextId());
    }
}
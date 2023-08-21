package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import httpservice.HttpTaskServer;
import httpservice.KVServer;
import httpservice.LocalDateTimeAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import taskmanager.HttpTaskManager;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HttpManagerTest {
    private static final int PORT = 8080;
    KVServer kvServer;
    HttpServer httpServer;
    TaskManager manager;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();

        manager = Managers.getDefault();

        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HttpTaskServer.TasksHandler(manager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        httpServer.stop(1);
    }

    public void createTasks() {
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
    }


    @Test
    public void saveAndLoadTest() {
        createTasks();

        HttpTaskManager managerFromServer = HttpTaskManager.loadFromServer("http://localhost:8078/");

        assertEquals(manager.getAllTasks(), managerFromServer.getAllTasks(), "Проблема в загрузке с сервера.");
        assertEquals(manager.getAllSubtasks(), managerFromServer.getAllSubtasks(), "Проблема в " +
                "загрузке с сервера.");
        assertEquals(manager.getAllEpics(), managerFromServer.getAllEpics(), "Проблема в загрузке с сервера.");
        assertEquals(manager.getHistory(), managerFromServer.getHistory(), "Проблема в загрузке с сервера.");
    }

    @Test
    public void getTasksTest() {
        createTasks();
        String tasksLoaded = load("tasks/task/");
        String tasks = gson.toJson(manager.getAllTasks()).toString();

        assertEquals(tasks, tasksLoaded);
    }

    @Test
    public void getEpicsTest() {
        createTasks();
        String epicsLoaded = load("tasks/epic/");
        String epics = gson.toJson(manager.getAllEpics()).toString();

        assertEquals(epics, epicsLoaded);
    }

    @Test
    public void getSubtasksTest() {
        createTasks();
        String subtasksLoaded = load("tasks/subtask/");
        String subtasks = gson.toJson(manager.getAllSubtasks()).toString();

        assertEquals(subtasks, subtasksLoaded);
    }

    @Test
    public void getHistoryTest() {
        createTasks();
        String historyLoaded = load("tasks/history/");
        String history = gson.toJson(manager.getHistory()).toString();

        assertEquals(history, historyLoaded);
    }

    @Test
    public void getPrioritizedTest() {
        createTasks();
        String prioritizedLoaded = load("tasks/");
        String prioritized = gson.toJson(manager.getPrioritizedTasks()).toString();

        assertEquals(prioritized, prioritizedLoaded);
    }

     @Test
     public void getTaskByIdTest() {
         createTasks();
         String taskString = load("tasks/task/?id=1");
         JsonElement jsonElementTask = JsonParser.parseString(taskString);
         Task taskLoaded = gson.fromJson(jsonElementTask, Task.class);

         Task task = manager.getTaskById(1);

         assertEquals(task, taskLoaded);
     }

     @Test
     public void getEpicByIdTest() {
         createTasks();
         String epicString = load("tasks/epic/?id=3");
         JsonElement jsonElementEpic = JsonParser.parseString(epicString);
         Epic epicLoaded = gson.fromJson(jsonElementEpic, Epic.class);

         Epic epic = manager.getEpicById(3);

         assertEquals(epic, epicLoaded);
     }

     @Test
     public void getSubtaskByIdTest() {
         createTasks();
         String subtaskString = load("tasks/subtask/?id=7");
         JsonElement jsonElementSubtask = JsonParser.parseString(subtaskString);
         Subtask subtaskLoaded = gson.fromJson(jsonElementSubtask, Subtask.class);

         Subtask subtask = manager.getSubtaskById(7);

         assertEquals(subtask, subtaskLoaded);
     }

     @Test
     public void getSubtasksByEpicTest() {
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

         String subtasksLoaded = load("tasks/subtask/epic/?id=4");
         String subtasks = gson.toJson(manager.getSubTasksByEpic(epic2)).toString();

         assertEquals(subtasks, subtasksLoaded);
     }

     @Test
     public void removeAllTasksTest() {
         createTasks();
         remove("tasks/task/");

         assertEquals("[]", manager.getAllTasks().toString());
     }

     @Test
     public void removeAllEpicsTest() {
         createTasks();
         remove("tasks/epic/");

         assertEquals("[]", manager.getAllEpics().toString());
     }

     @Test
     public void removeAllSubtasksTest() {
         createTasks();
         remove("tasks/subtask/");

         assertEquals("[]", manager.getAllSubtasks().toString());
     }

     @Test
     public void removeTaskByIdTest() {
         createTasks();
         remove("tasks/task/?id=1");

         assertNull(manager.getTaskById(1));
     }

     @Test
     public void removeEpicByIdTest() {
         createTasks();
         remove("tasks/epic/?id=3");

         assertNull(manager.getEpicById(3));
     }

     @Test
     public void removeSubtaskByIdTest() {
         createTasks();
         remove("tasks/subtask/?id=6");

         assertNull(manager.getSubtaskById(6));
     }

     @Test
     public void addTaskTest() {
         HttpClient client = HttpClient.newHttpClient();
         URI url = URI.create("http://localhost:8080/tasks/task/");
         Task newTask = new Task("Task1: название", "Task1: описание",
                 LocalDateTime.parse("2023-07-30T00:21:21"), 20);
         String json = gson.toJson(newTask);
         final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
         HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();;

         try {
             HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

             if (response.statusCode() != 200) {
                 System.out.println("Произошла ошибка, код ответа сервера: " + response.statusCode());
             }
         } catch (IOException | InterruptedException e) {
             System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                     "Проверьте, пожалуйста, адрес и повторите попытку.");
         }

         assertEquals(2, manager.getNextId());
         assertEquals("Task1: название", manager.getTaskById(1).getTitle());
     }

    @Test
    public void addEpicTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic newEpic = new Epic("Epic1: название", "Epic1: описание");
        String json = gson.toJson(newEpic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();;

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Произошла ошибка, код ответа сервера: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        assertEquals(2, manager.getNextId());
        assertEquals("Epic1: название", manager.getEpicById(1).getTitle());
    }

    @Test
    public void addSubtaskTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Subtask newSubtask = new Subtask("Subtask3: название", "Subtask3: описание",
                LocalDateTime.parse("2023-07-30T12:21:21"), 5);
        String json = gson.toJson(newSubtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();;

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Произошла ошибка, код ответа сервера: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        assertEquals(2, manager.getNextId());
        assertEquals("Subtask3: название", manager.getSubtaskById(1).getTitle());
    }

    public String load(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + endpoint);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Произошла ошибка, код ответа сервера: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return null;
    }

    public void remove(String endpoint) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + endpoint);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Произошла ошибка, код ответа сервера: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}

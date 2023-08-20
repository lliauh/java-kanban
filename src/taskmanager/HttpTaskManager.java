package taskmanager;

import com.google.gson.*;
import exceptions.ManagerSaveException;
import httpservice.KVTaskClient;
import httpservice.LocalDateTimeAdapter;
import task.Epic;
import task.Subtask;
import task.Task;
import task.Type;

import java.io.IOException;
import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager {
    private final String serverURL;
    private static KVTaskClient client;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskManager(String serverURL) {
        super(serverURL);
        this.serverURL = serverURL;
        client = new KVTaskClient(serverURL);
    }
    @Override
    public void save() {
        client.put("tasks", gson.toJson(getAllTasks()));
        client.put("epics", gson.toJson(getAllEpics()));
        client.put("subtasks", gson.toJson(getAllSubtasks()));
        client.put("history", gson.toJson(getHistory()));
        client.put("nextId", Integer.toString(getNextId()));
    }

    public static HttpTaskManager loadFromServer(String serverURL) {
        HttpTaskManager manager = new HttpTaskManager(serverURL);

        String tasksJson = client.load("tasks");
        String epicsJson = client.load("epics");
        String subtasksJson = client.load("subtasks");
        String historyJson = client.load("history");
        String nextIdString = client.load("nextId");

        JsonElement jsonElementTasks = JsonParser.parseString(tasksJson);
        JsonArray jsonArrayTasks = jsonElementTasks.getAsJsonArray();
        for (int i = 0; i < jsonArrayTasks.size(); i++) {
            JsonElement taskJson = jsonArrayTasks.get(i);
            Task task = gson.fromJson(taskJson, Task.class);
            manager.updateTask(task);
        }

        JsonElement jsonElementEpics = JsonParser.parseString(epicsJson);
        JsonArray jsonArrayEpics = jsonElementEpics.getAsJsonArray();
        for (int i = 0; i < jsonArrayEpics.size(); i++) {
            JsonElement epicJson = jsonArrayEpics.get(i);
            Epic epic = gson.fromJson(epicJson, Epic.class);
            manager.updateEpic(epic);
        }

        JsonElement jsonElementSubtasks = JsonParser.parseString(subtasksJson);
        JsonArray jsonArraySubtasks = jsonElementSubtasks.getAsJsonArray();
        for (int i = 0; i < jsonArraySubtasks.size(); i++) {
            JsonElement subtaskJson = jsonArraySubtasks.get(i);
            Subtask subtask = gson.fromJson(subtaskJson, Subtask.class);
            manager.updateSubtask(subtask);
        }

        JsonElement jsonElementHistory = JsonParser.parseString(historyJson);
        JsonArray jsonArrayHistory = jsonElementHistory.getAsJsonArray();
        for (int i = 0; i < jsonArrayHistory.size(); i++) {
            JsonElement taskFromHistoryJson = jsonArrayHistory.get(i);
            Task task = gson.fromJson(taskFromHistoryJson, Task.class);
            if (task.getType() == Type.TASK) {
                manager.getTaskById(task.getId());
            } else if (task.getType() == Type.EPIC) {
                manager.getEpicById(task.getId());
            } else if (task.getType() == Type.SUBTASK) {
                manager.getSubtaskById(task.getId());
            }
        }

        manager.nextId = Integer.parseInt(nextIdString);

        return manager;
    }
}

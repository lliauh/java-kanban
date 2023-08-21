package httpservice;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import task.*;
import taskmanager.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();


    public static void main(String[] args) throws IOException {
        new KVServer().start();
        TaskManager manager = Managers.getDefault();
        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static class TasksHandler implements HttpHandler {
        TaskManager manager;
        public TasksHandler(TaskManager manager) {
            this.manager = manager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            EnpointMethodsEnum enpointMethodsEnum;
            String requestPath = exchange.getRequestURI().getPath();
            String requestQuery = exchange.getRequestURI().getQuery();

            switch (exchange.getRequestMethod()) {
                case "GET":
                    enpointMethodsEnum = getEndpointGet(requestPath, requestQuery);
                    break;
                case "POST":
                    enpointMethodsEnum = getEndpointPost(requestPath, requestQuery);
                    break;
                case "DELETE":
                    enpointMethodsEnum = getEndpointDelete(requestPath, requestQuery);
                    break;
                default:
                    enpointMethodsEnum = EnpointMethodsEnum.UNKNOWN;
            }

            switch (enpointMethodsEnum) {
                case GET_PRIOR_TASKS:
                    handleGetPriorTasks(exchange);
                    break;
                case GET_HISTORY:
                    handleGetHistory(exchange);
                    break;
                case GET_TASKS:
                    handleGetTasks(exchange);
                    break;
                case GET_EPICS:
                    handleGetEpics(exchange);
                    break;
                case GET_SUBTASKS:
                    handleGetSubtasks(exchange);
                    break;
                case GET_TASK_BY_ID:
                    handleGetTaskById(exchange);
                    break;
                case GET_EPIC_BY_ID:
                    handleGetEpicById(exchange);
                    break;
                case GET_SUBTASK_BY_ID:
                    handleGetSubTaskById(exchange);
                    break;
                case GET_SUBTASKS_BY_EPIC:
                    handleGetSubtasksByEpic(exchange);
                    break;
                case POST_TASK:
                    handlePostTask(exchange);
                    break;
                case POST_EPIC:
                    handlePostEpic(exchange);
                    break;
                case POST_SUBTASK:
                    handlePostSubtask(exchange);
                    break;
                case DELETE_TASKS:
                    handleDeleteTasks(exchange);
                    break;
                case DELETE_EPICS:
                    handleDeleteEpics(exchange);
                    break;
                case DELETE_SUBTASKS:
                    handleDeleteSubtasks(exchange);
                    break;
                case DELETE_TASK_BY_ID:
                    handleDeleteTaskById(exchange);
                    break;
                case DELETE_EPIC_BY_ID:
                    handleDeleteEpicById(exchange);
                    break;
                case DELETE_SUBTASK_BY_ID:
                    handleDeleteSubtaskById(exchange);
                    break;
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        }

        private EnpointMethodsEnum getEndpointGet(String requestPath, String requestQuery) {
            String[] pathSplitted = requestPath.split("/");
            int pathLength = pathSplitted.length;
            String query = requestQuery;

            if (pathLength == 2 && pathSplitted[1].equals("tasks")) {
                return EnpointMethodsEnum.GET_PRIOR_TASKS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("history")) {
                return EnpointMethodsEnum.GET_HISTORY;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("task")
                    && query == null) {
                return EnpointMethodsEnum.GET_TASKS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("epic")
                    && query == null) {
                return EnpointMethodsEnum.GET_EPICS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("subtask")
                    && query == null) {
                return EnpointMethodsEnum.GET_SUBTASKS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("task")
                    && query.startsWith("id=")) {
                return EnpointMethodsEnum.GET_TASK_BY_ID;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("epic")
                    && query.startsWith("id=")) {
                return EnpointMethodsEnum.GET_EPIC_BY_ID;
            }

            if (pathLength ==3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("subtask")
                    && query.startsWith("id=")) {
                return EnpointMethodsEnum.GET_SUBTASK_BY_ID;
            }

            if (pathLength == 4 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("subtask")
                    && pathSplitted[3].equals("epic") && query.startsWith("id=")) {
                return EnpointMethodsEnum.GET_SUBTASKS_BY_EPIC;
            }

            return EnpointMethodsEnum.UNKNOWN;
        }

        private EnpointMethodsEnum getEndpointPost(String requestPath, String requestQuery) {
            String[] pathSplitted = requestPath.split("/");
            int pathLength = pathSplitted.length;
            String query = requestQuery;

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("task")) {
                return EnpointMethodsEnum.POST_TASK;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("epic")) {
                return EnpointMethodsEnum.POST_EPIC;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("subtask")) {
                return EnpointMethodsEnum.POST_SUBTASK;
            }

            return EnpointMethodsEnum.UNKNOWN;
        }

        private EnpointMethodsEnum getEndpointDelete(String requestPath, String requestQuery) {
            String[] pathSplitted = requestPath.split("/");
            int pathLength = pathSplitted.length;
            String query = requestQuery;

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("task")
                    && query == null) {
                return EnpointMethodsEnum.DELETE_TASKS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("epic")
                    && query == null) {
                return EnpointMethodsEnum.DELETE_EPICS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("subtask")
                    && query == null) {
                return EnpointMethodsEnum.DELETE_SUBTASKS;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("task")
                    && query.startsWith("id=")) {
                return EnpointMethodsEnum.DELETE_TASK_BY_ID;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("epic")
                    && query.startsWith("id=")) {
                return EnpointMethodsEnum.DELETE_EPIC_BY_ID;
            }

            if (pathLength == 3 && pathSplitted[1].equals("tasks") && pathSplitted[2].equals("subtask")
                    && query.startsWith("id=")) {
                return EnpointMethodsEnum.DELETE_SUBTASK_BY_ID;
            }

            return EnpointMethodsEnum.UNKNOWN;
        }

        private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
            if (responseString.isBlank()) {
                exchange.sendResponseHeaders(responseCode, 0);
            } else {
                byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                exchange.sendResponseHeaders(responseCode, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
            exchange.close();
        }


        private void handleGetPriorTasks(HttpExchange exchange) throws IOException {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            writeResponse(exchange, gson.toJson(prioritizedTasks), 200);
        }

        private void handleGetHistory(HttpExchange exchange) throws IOException {
            List<Task> history = manager.getHistory();
            writeResponse(exchange, gson.toJson(history), 200);
        }

        private void handleGetTasks(HttpExchange exchange) throws IOException {
            List<Task> tasks = manager.getAllTasks();
            writeResponse(exchange, gson.toJson(tasks), 200);
        }

        private void handleGetEpics(HttpExchange exchange) throws IOException {
            List<Epic> epics = manager.getAllEpics();
            writeResponse(exchange, gson.toJson(epics), 200);
        }

        private void handleGetSubtasks(HttpExchange exchange) throws IOException {
            List<Subtask> subtasks = manager.getAllSubtasks();
            writeResponse(exchange, gson.toJson(subtasks), 200);
        }

        private void handleGetTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор таска", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            Optional<Task> task = Optional.ofNullable(manager.getTaskById(taskId));

            if (task.isEmpty()) {
                writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден",
                        404);
            } else {
                writeResponse(exchange, gson.toJson(task.get()), 200);
            }
        }

        private void handleGetEpicById(HttpExchange exchange) throws IOException {
            Optional<Integer> epicIdOpt = getTaskId(exchange);

            if (epicIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }

            int epicId = epicIdOpt.get();
            Optional<Epic> epic = Optional.ofNullable(manager.getEpicById(epicId));

            if (epic.isEmpty()) {
                writeResponse(exchange, "Эпик с идентификатором " + epicId + " не найден",
                        404);
            } else {
                writeResponse(exchange, gson.toJson(epic.get()), 200);
            }
        }

        private void handleGetSubTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> subtaskIdOpt = getTaskId(exchange);

            if (subtaskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор сабтаска", 400);
                return;
            }

            int subtaskId = subtaskIdOpt.get();
            Optional<Subtask> subtask = Optional.ofNullable(manager.getSubtaskById(subtaskId));

            if (subtask.isEmpty()) {
                writeResponse(exchange, "Сабтаск с идентификатором " + subtaskId + " не найден",
                        404);
            } else {
                writeResponse(exchange, gson.toJson(subtask.get()), 200);
            }
        }

        private void handleGetSubtasksByEpic(HttpExchange exchange) throws IOException {
            Optional<Integer> epicIdOpt = getTaskId(exchange);

            if (epicIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }

            int epicId = epicIdOpt.get();
            Optional<Epic> epic = Optional.ofNullable(manager.getEpicById(epicId));

            if (epic.isEmpty()) {
                writeResponse(exchange, "Эпик с идентификатором " + epicId + " не найден",
                        404);
            } else {
                List<Subtask> subtasks = manager.getSubTasksByEpic(epic.get());
                writeResponse(exchange, gson.toJson(subtasks), 200);
            }
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            Task task = gson.fromJson(getRequestBody(exchange), Task.class);

            if (task.getId() == null || manager.getNextId() <= task.getId()) {
                Integer taskId = manager.createTask(task);
                if (taskId == null) {
                    writeResponse(exchange, "Не удалось создать таск, пересечение по времени " +
                            "с уже существующими", 400);
                }
                writeResponse(exchange, "Таск успешно создан, его id: " + taskId, 200);
            } else {
                manager.updateTask(task);
                writeResponse(exchange, "Таск id: " + task.getId() + " был успешно обновлен",
                        200);
            }
        }

        private void handlePostEpic(HttpExchange exchange) throws IOException {
            Epic epic = gson.fromJson(getRequestBody(exchange), Epic.class);

            if (epic.getId() == null || manager.getNextId() <= epic.getId()) {
                Integer epicId = manager.createEpic(epic);
                if (epicId == null) {
                    writeResponse(exchange, "Не удалось создать эпик, пересечение по времени " +
                            "с уже существующими", 400);
                }
                writeResponse(exchange, "Эпик успешно создан, его id: " + epicId, 200);
            } else {
                manager.updateEpic(epic);
                writeResponse(exchange, "Эпик id: " + epic.getId() + " был успешно обновлен",
                        200);
            }
        }

        private void handlePostSubtask(HttpExchange exchange) throws IOException {
            Subtask subtask = gson.fromJson(getRequestBody(exchange), Subtask.class);

            if (subtask.getId() == null || manager.getNextId() <= subtask.getId()) {
                Integer subtaskId = manager.createSubtask(subtask, subtask.getEpicId());
                if (subtaskId == null) {
                    writeResponse(exchange, "Не удалось создать эпик, пересечение по времени " +
                            "с уже существующими", 400);
                }
                writeResponse(exchange, "Сабтаск успешно создан, его id: " + subtaskId, 200);
            } else {
                manager.updateSubtask(subtask);
                writeResponse(exchange, "Сабтаск id: " + subtask.getId() + " был успешно обновлен",
                        200);
            }
        }

        private void handleDeleteTasks(HttpExchange exchange) throws IOException {
            manager.removeAllTasks();
            writeResponse(exchange, "Все таски были удалены", 200);
        }

        private void handleDeleteEpics(HttpExchange exchange) throws IOException {
            manager.removeAllEpics();
            writeResponse(exchange, "Все эпики были удалены", 200);
        }

        private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
            manager.removeAllSubtasks();
            writeResponse(exchange, "Все сабтаски были удалены", 200);
        }

        private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> taskIdOpt = getTaskId(exchange);

            if (taskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор таска", 400);
                return;
            }

            int taskId = taskIdOpt.get();
            Optional<Task> task = Optional.ofNullable(manager.getTaskById(taskId));

            if (task.isEmpty()) {
                writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден",
                        404);
            } else {
                manager.removeTaskById(taskId);
                writeResponse(exchange, "Таск с идентификатором " + taskId + " был удален",
                        200);
            }
        }

        private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
            Optional<Integer> epicIdOpt = getTaskId(exchange);

            if (epicIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                return;
            }

            int epicId = epicIdOpt.get();
            Optional<Epic> epic = Optional.ofNullable(manager.getEpicById(epicId));

            if (epic.isEmpty()) {
                writeResponse(exchange, "Эпик с идентификатором " + epicId + " не найден",
                        404);
            } else {
                manager.removeEpicById(epicId);
                writeResponse(exchange, "Эпик с идентификатором " + epicId + " был удален",
                        200);
            }
        }

        private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
            Optional<Integer> subtaskIdOpt = getTaskId(exchange);

            if (subtaskIdOpt.isEmpty()) {
                writeResponse(exchange, "Некорректный идентификатор сабтаска", 400);
                return;
            }

            int subtaskId = subtaskIdOpt.get();
            Optional<Subtask> subtask = Optional.ofNullable(manager.getSubtaskById(subtaskId));

            if (subtask.isEmpty()) {
                writeResponse(exchange, "Сабтаск с идентификатором " + subtaskId + " не найден",
                        404);
            } else {
                manager.removeSubtaskById(subtaskId);
                writeResponse(exchange, "Сабтаск с идентификатором " + subtaskId + " был удален",
                        200);
            }
        }

        private Optional<Integer> getTaskId(HttpExchange exchange) {
            String query = exchange.getRequestURI().getQuery();
            String[] querySplitted = query.split("=");

            try {
                return Optional.of(Integer.parseInt(querySplitted[1]));
            } catch (NumberFormatException exception) {
                return Optional.empty();
            }
        }

        private String getRequestBody(HttpExchange exchange) throws IOException {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            return body;
        }
    }
}

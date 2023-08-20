package taskmanager;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static TaskManager getFileBackedManager() {
        return new FileBackedTasksManager("src/output/http_test.csv");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

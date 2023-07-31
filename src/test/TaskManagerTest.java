package test;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import taskmanager.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;

    public abstract void createManager();

    @Test
    public void testCreateTask() {
        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testCreateEpic() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);

        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testCreateSubtask() {
        Subtask subtask = new Subtask("Subtask1", "Subtask1 description");
        Epic epic = new Epic("Epic1", "Epic1 description");
        final int epicId = taskManager.createEpic(epic);
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
        assertNotNull(savedSubtask.getEpicId(), "У сабтаска не сохранен эпик.");
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);
        savedTask.setStatus(Status.IN_PROGRESS);
        savedTask.setDescription("Updated description");
        taskManager.updateTask(savedTask);

        final Task updatedTask = taskManager.getTaskById(taskId);

        assertEquals("Updated description", updatedTask.getDescription(), "Описание не обновилось.");
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus(), "Статус не обновился.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(updatedTask, tasks.get(0), "Обновленные задачи не совпадают.");
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask1 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        final Epic savedEpic = taskManager.getEpicById(epicId);
        taskManager.getSubtaskById(subtaskId).setStatus(Status.IN_PROGRESS);
        savedEpic.setDescription("Updated Epic description");
        taskManager.updateEpic(savedEpic);

        final Epic updatedEpic = taskManager.getEpicById(epicId);

        assertEquals("Updated Epic description", updatedEpic.getDescription(), "Описание не обновилось.");
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Статус не обновился.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertEquals(updatedEpic, epics.get(0), "Обновленные задачи не совпадают.");
    }

    @Test
    public void testUpdateSubtask() {
        Epic epic = new Epic("Epic1", "Epic1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask1", "Subtask1 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        savedSubtask.setStatus(Status.DONE);
        savedSubtask.setTitle("Updated Subtask title");
        taskManager.updateSubtask(savedSubtask);

        final Subtask updatedSubtask = taskManager.getSubtaskById(subtaskId);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals("Updated Subtask title", updatedSubtask.getTitle(), "Описание не обновилось.");
        assertEquals(Status.DONE, updatedSubtask.getStatus(), "Статус не обновился.");
        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус эпика не обновился.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(updatedSubtask, subtasks.get(0), "Обновленные задачи не совпадают.");
    }

    @Test
    public void testRemoveAllTasks() {
        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);

        Task task2 = new Task("Test2", "Test2 description");
        final int task2Id = taskManager.createTask(task2);

        taskManager.removeAllTasks();

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(0, tasks.size(), "Удаление задач не сработало.");
    }

    @Test
    public void testRemoveAllEpics() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);

        Epic epic2 = new Epic("Test2", "Test2 description");
        final int epic2Id = taskManager.createEpic(epic2);

        Subtask subtask = new Subtask("Test3", "Test3 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        taskManager.removeAllEpics();

        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(0, epics.size(), "Удаление эпиков не сработало.");
        assertEquals(0, subtasks.size(), "Удаление сабтасков не сработало.");
    }

    @Test
    public void testRemoveAllSubtasks() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test3", "Test3 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        Subtask subtask2 = new Subtask("Test4", "Test4 description");
        final int subtask2Id = taskManager.createSubtask(subtask2, epicId);

        taskManager.removeAllSubtasks();

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        Epic updatedEpic = taskManager.getEpicById(epicId);

        assertEquals(0, subtasks.size(), "Удаление сабтасков не сработало.");
        assertEquals(0, updatedEpic.getSubtasks().size(), "ID сабтаска не был удален у эпика.");
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Таск не возвращается по ID.");
        assertEquals(task, savedTask, "По ID возвращается другой таск.");
        assertNull(taskManager.getTaskById(10), "Таск возвращается по несуществующему ID.");
    }

    @Test
    public void testGetEpicById() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);

        Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не возвращается по ID.");
        assertEquals(epic, savedEpic, "По ID возвращается другой эпик.");
        assertNull(taskManager.getEpicById(10), "Эпик возвращается по несуществующему ID.");
    }

    @Test
    public void testGetSubtaskById() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test2", "Test2 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Сабтаск не возвращается по ID.");
        assertEquals(subtask, savedSubtask, "По ID возвращается другой сабтаск.");
        assertNull(taskManager.getSubtaskById(10), "Сабтаск возвращается по несуществующему ID.");
    }

    @Test
    public void testRemoveTaskById() {
        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);
        taskManager.removeTaskById(taskId);

        assertNull(taskManager.getTaskById(taskId), "Таск не был удалён по ID.");
    }

    @Test
    public void testRemoveSubtaskById() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test2", "Test2 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);
        subtask.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.removeSubtaskById(subtaskId);

        assertNull(taskManager.getSubtaskById(subtaskId), "Сабтаск не был удалён по ID.");
        assertEquals(0, taskManager.getSubTasksByEpic(epic).size(), "Список сабстасков в эпике не был очищен.");
        assertEquals(Status.NEW, taskManager.getEpicById(epicId).getStatus(), "Статус эпика не был обновлён.");
    }

    @Test
    public void testRemoveEpicById() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test2", "Test2 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        taskManager.removeEpicById(epicId);

        assertNull(taskManager.getEpicById(epicId), "Эпик не был удалён по ID.");
        assertNull(taskManager.getSubtaskById(subtaskId), "Сабтаск не был удалён при удалении эпика.");
    }

    @Test
    public void testGetSubTasksByEpic() {
        Epic epic = new Epic("Test1", "Test1 description");
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test2", "Test2 description");
        final int subtaskId = taskManager.createSubtask(subtask, epicId);

        List<Subtask> subtasks = taskManager.getSubTasksByEpic(epic);

        assertNotNull(subtasks, "Список сабтасков не возвращается.");
        assertEquals(subtask, subtasks.get(0), "Возвращается другой список сабтасков.");
    }

    @Test
    public void testGetHistory() {
        List<Task> history = taskManager.getHistory();
        List<Task> emptyHistory = new ArrayList<>();
        assertEquals(emptyHistory, history, "Возвращается непустая история при отсутствии операций с задачами.");

        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);
        taskManager.getTaskById(taskId);

        history = taskManager.getHistory();

        assertNotNull(history, "Возвращается пустая история задач.");
        assertEquals(task, history.get(0), "Из истории возвращается другая задача.");
    }

    @Test
    public void testGetNextId() {
        Task task = new Task("Test1", "Test1 description");
        final int taskId = taskManager.createTask(task);
        final int nextId = taskManager.getNextId();

        assertNotNull(nextId, "Следующий ID не возвращается.");
        assertEquals(2, nextId, "Возвращается некорректное значение следующего ID.");
    }

    @Test
    public void testGetPrioritizedTasks() {
        Task task1 = new Task("Task1: название", "Task1: описание",
                LocalDateTime.parse("2023-07-30T00:51:21"), 20);
        Task task2 = new Task("Task2: название", "Task2: описание",
                LocalDateTime.parse("2023-07-30T00:19:21"), 19);
        Epic epic1 = new Epic("Epic1: название", "Epic1: описание");
        Epic epic2 = new Epic("Epic2: название", "Epic2: описание");
        Subtask subtask1 = new Subtask("Subtask1: название", "Subtask1: описание");
        Subtask subtask2 = new Subtask("Subtask2: название", "Subtask2: описание",
                LocalDateTime.parse("2023-07-30T08:21:21"), 20);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask1, epic2.getId());
        taskManager.createSubtask(subtask2, epic2.getId());

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(task2, prioritizedTasks.get(0), "Приоритизация задач не работает корректно");
        assertEquals(task1, prioritizedTasks.get(1), "Приоритизация задач не работает корректно");
        assertEquals(subtask2, prioritizedTasks.get(2), "Приоритизация задач не работает корректно");
        assertEquals(subtask1, prioritizedTasks.get(3), "Приоритизация задач не работает корректно");
    }

    @Test
    public void testTasksTimeIntersectionValidate() {
        Task task1 = new Task("Task1: название", "Task1: описание",
                LocalDateTime.parse("2023-07-30T00:20:00"), 20);
        Task task2 = new Task("Task2: название", "Task2: описание",
                LocalDateTime.parse("2023-07-30T00:19:21"), 19);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task1, taskManager.getAllTasks().get(0));
    }
}

package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import taskmanager.HistoryManager;
import taskmanager.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;


    @BeforeEach
    public void createManagerAndTasks() {
        historyManager = new InMemoryHistoryManager();

        task1 = new Task("Task1", "Task1 description");
        task1.setId(1);

        task2 = new Task("Task2", "Task2 description");
        task2.setId(2);

        task3 = new Task("Task3", "Task3 description");
        task3.setId(3);

        task4 = new Task("Task4", "Task4 description");
        task4.setId(4);
    }

    @Test
    public void testAdd() {
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size(), "Задача не добавляется в историю.");
        assertEquals(task1, historyManager.getHistory().get(0), "Задача из истории не совпадает с добавляемой.");

        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size(), "Более 1-й задачи не добавляется в историю.");
    }

    @Test
    public void testRemove() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task2.getId());

        assertEquals(1, historyManager.getHistory().size(), "Задача не удаляется из истории.");
        assertEquals(task1, historyManager.getHistory().get(0), "Некорректно отрабатывает удаление.");
    }

    @Test
    public void testGetHistory() {
        List<Task> tasks  = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> historyList = historyManager.getHistory();

        assertEquals(tasks, historyList, "Некорректно возвращается история.");
    }

    @Test
    public void emptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая.");
    }

    @Test
    public void duplicationTest() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(4, historyManager.getHistory().size(), "Задачи в истории дублируются.");
    }

    @Test
    public void removeFromTheStart() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        historyManager.remove(task1.getId());

        assertEquals(3, historyManager.getHistory().size(), "Некорректно отрабатывает удаление: " +
                "начало истории.");
    }

    @Test
    public void removeFromTheMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        assertEquals(2, historyManager.getHistory().size(), "Некорректно отрабатывает удаление: " +
                "середина истории.");
    }

    @Test
    public void removeFromTheEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);

        historyManager.remove(task4.getId());

        assertEquals(3, historyManager.getHistory().size(), "Некорректно отрабатывает удаление: " +
                "конец истории.");
    }
}

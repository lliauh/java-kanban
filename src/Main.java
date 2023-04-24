import taskmanager.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Таск1: первое название", "Таск1: первое описание");
        Task task2 = new Task("Таск2: второе название", "Таск2: второе описание");

        Epic epic1 = new Epic("Эпик1", "Описание Эпика1");
        Epic epic2 = new Epic("Эпик2", "Описание Эпика2");

        Subtask subtask1 = new Subtask("Сабтаск1", "Описание сабтаска1");
        Subtask subtask2 = new Subtask("Сабтаск2", "Описание сабтаска2");
        Subtask subtask3 = new Subtask("Сабтаск3", "Описание сабтаска3");
        Subtask subtask4 = new Subtask("Сабтаск4", "Описание сабтаска4");

        Epic epic3 = new Epic("Эпик3", "Описание Эпика3");

        Subtask subtask5 = new Subtask("Сабтаск5", "Описание сабтаска5");
        Subtask subtask6 = new Subtask("Сабтаск6", "Описание сабтаска6");

        manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic1.getId());
        manager.createSubtask(subtask4, epic1.getId());

        manager.createEpic(epic3);
        manager.createSubtask(subtask5, epic3.getId());
        manager.createSubtask(subtask6, epic3.getId());



        System.out.println("Проверяем создание и получение всех задач");
        System.out.println("Таски:");
        System.out.println(manager.getAllTasks());
        System.out.println("--- --- ---");
        System.out.println("Эпики:");
        System.out.println(manager.getAllEpics());
        System.out.println("--- --- ---");
        System.out.println("Сабтаски:");
        System.out.println(manager.getAllSubtasks());

        System.out.println("--- --- ---");
        System.out.println("--- --- ---");

        System.out.println("Проверяем получение таска по id: " + manager.getTaskById(1));
        System.out.println("Проверяем получение эпика по id: " + manager.getEpicById(3));
        System.out.println("Проверяем получение истории: " + manager.getHistory());
        System.out.println("Проверяем получение сабтаска по id: " + manager.getSubtaskById(5));
        System.out.println("Проверяем получение таска по id: " + manager.getTaskById(2));
        System.out.println("Проверяем получение истории: " + manager.getHistory());
        System.out.println("Проверяем получение эпика по id: " + manager.getEpicById(4));
        System.out.println("Проверяем получение сабтаска по id: " + manager.getSubtaskById(6));
        System.out.println("Проверяем получение истории: " + manager.getHistory());

        System.out.println("--- --- ---");
        System.out.println("--- --- ---");

        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        System.out.println("Проверяем обновление таска - " + manager.getTaskById(1));
        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);
        subtask4.setStatus(Status.DONE);
        manager.updateSubtask(subtask4);
        System.out.println("Проверяем обновление сабтаска - " + manager.getSubtaskById(5));
        System.out.println("Проверяем обновление эпика с сабтасками DONE - " + manager.getEpicById(3));
        System.out.println("Проверяем обновление эпика без сабтасков - " + manager.getEpicById(4));

        System.out.println("--- --- ---");
        System.out.println("--- --- ---");

        System.out.println("Проверяем получение списка всех сабтасок у эпика - " + manager.getSubTasksByEpic(epic1));

        System.out.println("--- --- ---");
        System.out.println("--- --- ---");

        manager.removeTaskById(1);
        System.out.println("Проверяем удаление таски по ID - " + manager.getTaskById(1));
        manager.removeSubtaskById(5);
        System.out.println("Проверяем удаление сабтаски по ID - " + manager.getTaskById(5));
        System.out.println("Обновился список сабтасок в эпике? - " + manager.getTaskById(3));
        manager.removeEpicById(3);
        System.out.println("Проверяем удаление эпика по ID - " + manager.getTaskById(3));
        System.out.println("Сабтаски эпика тоже снесли? Если да, то останется только 2 - " + manager.getAllSubtasks());


        System.out.println("--- --- ---");
        System.out.println("--- --- ---");

        System.out.println("Проверяем удаление всех задач");
        System.out.println("Удаляем таски:");
        manager.removeAllTasks();
        System.out.println("Что у нас в тасках? - " + manager.getAllTasks());
        System.out.println("--- --- ---");
        System.out.println("Удаляем эпики:");
        manager.removeAllEpics();
        System.out.println("Что у нас в эпиках? - " + manager.getAllEpics());
        System.out.println("Обновились идентификаторы эпиков в сабтасках? - " + manager.getAllSubtasks());
        System.out.println("--- --- ---");
        System.out.println("Удаляем сабтаски:");
        manager.removeAllSubtasks();
        System.out.println("Что у нас в сабтасках? - " + manager.getAllSubtasks());
    }
}
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


        manager.createTask(task1);
        manager.createTask(task2);

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.createSubtask(subtask1, epic1.getId());
        manager.createSubtask(subtask2, epic1.getId());
        manager.createSubtask(subtask3, epic1.getId());

        System.out.println("Проверяем вывод истории:");

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

        System.out.println(manager.getHistory());

        System.out.println("Удалили задачу id 1, проверяем:");
        manager.removeTaskById(1);
        System.out.println(manager.getHistory());

        System.out.println("Удалили эпик с 3-мя подзачами (id 3), проверяем:");
        manager.removeEpicById(3);
        System.out.println(manager.getHistory());
    }
}
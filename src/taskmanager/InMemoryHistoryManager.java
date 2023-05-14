package taskmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private class CustomLinkedList<Task>{
        private Node<Task> head;
        private Node<Task> tail;
        private int size = 0;

        public Node<Task> linkLast(Task element) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;

            return newNode;
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();

            Node<Task> element = head;
            for (int i = 0; i < size; i++) {
                tasks.add(element.data);
                element = element.next;
            }

            return tasks;
        }

        public void removeNode(Node<Task> node) {
            if (node == head) {
                head = node.next;
                node.next.prev = null;
                size--;
            } else if (node == tail) {
                tail = node.prev;
                node.prev.next = null;
                size--;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                size--;
            }
        }
    }

    private CustomLinkedList<Task> historyList = new CustomLinkedList<>();
    private Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId())) {
            historyList.removeNode(historyMap.get(task.getId()));
            historyMap.put(task.getId(), historyList.linkLast(task));
        } else {
            historyMap.put(task.getId(), historyList.linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        historyList.removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }
}

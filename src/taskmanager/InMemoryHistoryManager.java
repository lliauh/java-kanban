package taskmanager;

import task.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private class CustomLinkedList<Task>{
        private Node<Task> head;
        private Node<Task> tail;

        public Node<Task> linkLast(Task element) {
            Node<Task> oldTail = tail;
            Node<Task> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }

            return newNode;
        }

        public List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();

            Node<Task> element = head;

            while (element != null) {
                tasks.add(element.data);
                element = element.next;
            }

            return tasks;
        }

        public void removeNode(Node<Task> node) {
            if (node == head) {
                head = node.next;
                node.next.prev = null;
            } else if (node == tail) {
                tail = node.prev;
                node.prev.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
        }
    }

    private CustomLinkedList<Task> historyList = new CustomLinkedList<>();
    private Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void add(Task task) {
        if (historyMap.containsKey(task.getId()) && historyMap.size() != 1) {
            historyList.removeNode(historyMap.get(task.getId()));
            historyMap.put(task.getId(), historyList.linkLast(task));
        } else {
            historyMap.put(task.getId(), historyList.linkLast(task));
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            historyList.removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }
}

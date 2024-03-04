package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    protected Map<Integer, Node<Task>> historyList = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    protected int size = 0;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (historyList.containsKey(task.getId())) {
            removeNode(historyList.get(task.getId()));
        }
        historyList.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (historyList.containsKey(id)) {
            removeNode(historyList.get(id));
            historyList.remove(id);
        }
    }

    public List<Task> getTasks() {
        List<Task> historyArrayList = new ArrayList<>();
        for (Node<Task> node = head; node != null; node = node.next) {
            historyArrayList.add(node.data);
        }
        return historyArrayList;
    }

    protected void removeNode(Node<Task> node) {
        if (node.prev == null && node.next == null) {
            head = null;
            tail = null;
        } else if (node.prev == null) {
            node.next.prev = null;
            head = node.next;
            size--;
        } else if (node.next == null) {
            node.prev.next = null;
            tail = node.prev;
            size--;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }
    }

      protected static class Node<TaskT> {
        protected TaskT data;
        protected Node<TaskT> next;
        protected Node<TaskT> prev;

        public Node(Node<TaskT> prev, TaskT data, Node<TaskT> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    protected Node<Task> linkLast(Task task) {
        final Node<Task> oldLast = tail;
        final Node<Task> newNode = new Node<>(oldLast, task, null);
        tail = newNode;
        if (oldLast == null) {
            head = newNode;
        } else {
            oldLast.next = tail;
        }
        size++;
        return newNode;
    }
}

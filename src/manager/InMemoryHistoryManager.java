package manager;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodes = new HashMap<>();

    @Override
    public List<Task> getHistory() {

        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {

        removeNode(nodes.remove(id));
    }

    private void linkLast(Task item) {
        final Node<Task> oldTail = tail;
        final Node<Task> node = new Node<>(oldTail, item, null);
        tail = node;
        nodes.put(item.getId(), node);
        if (oldTail == null) {
            head = node;
        }else {
            oldTail.next = node;
        }
    }

    private List<Task> getTasks() {
        Node<Task> actualNode = head;
        List<Task> tasks = new ArrayList<>();
        while (actualNode != null) {

            tasks.add(actualNode.info);

            actualNode = actualNode.next;
        }

        return tasks;
    }

    private static class Node<Task> {
        public Task info;
        public Node<Task> prev;
        public Node<Task> next;

        public Node(Node<Task> prev, Task data, Node<Task> next) {
            this.info = data;
            this.next = next;
            this.prev = prev;
        }
    }
    private void removeNode(Node<Task> node) {

        if (node != null) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.info = null;

            if (head == node && tail == node) {
                head = null;

                tail = null;
            } else if (head == node) {
                head = next;
                head.prev = null;
            } else if (tail == node) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }

        }
    }
}


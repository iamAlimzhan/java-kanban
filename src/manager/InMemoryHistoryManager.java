package manager;
import com.sun.tools.javac.Main;
import tasks.MainTask;
import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<MainTask> head;
    private Node<MainTask> tail;
    private final Map<Integer, Node<MainTask>> nodes = new HashMap<>();

    @Override
    public List<MainTask> getHistory() {

        return getTasks();
    }

    @Override
    public void add(MainTask mainTask) {
        if (mainTask != null) {
            remove(mainTask.getId());
            linkLast(mainTask);
        }
    }

    @Override
    public void remove(int id) {

        removeNode(nodes.remove(id));
    }

    private void linkLast(MainTask item) {
        final Node<MainTask> oldTail = tail;
        final Node<MainTask> node = new Node<>(oldTail, item, null);
        tail = node;
        nodes.put(item.getId(), node);
        if (oldTail == null) {
            head = node;
        }else {
            oldTail.next = node;
        }
    }

    private List<MainTask> getTasks() {
        Node<MainTask> actualNode = head;
        List<MainTask> tasks = new ArrayList<>();
        while (actualNode != null) {

            tasks.add(actualNode.info);

            actualNode = actualNode.next;
        }

        return tasks;
    }

    private static class Node<MainTask> {
        public tasks.MainTask info;
        public Node<tasks.MainTask> prev;
        public Node<tasks.MainTask> next;

        public Node(Node<tasks.MainTask> prev, tasks.MainTask data, Node<tasks.MainTask> next) {
            this.info = data;
            this.next = next;
            this.prev = prev;
        }
    }
    private void removeNode(Node<MainTask> node) {

        if (node != null) {
            final Node<MainTask> next = node.next;
            final Node<MainTask> prev = node.prev;
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


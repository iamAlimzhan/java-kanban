package manager;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistory = new ArrayList<>();
    private Node<Task> head;
    private Node<Task> taail;
    private HashMap<Integer, Node<Task>> getTask;

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(taskHistory);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            taskHistory.add(task);
            if (taskHistory.size() > 10) {
                taskHistory.remove(0);
            }
        }
    }

    @Override
    public void remove(int id) {
        taskHistory.remove(id);
    }

    private void linkLast(Task item) {
        final Node<Task> oldTail = taail;
        final Node<Task> node = new Node<Task>(oldTail, item, null);
        taail = node;
        getTask.put(item.getId(), node);
        if (oldTail == null)
            head = node;
        else
            oldTail.next = node;
    }

    private List<Task> getTasks() {
        Node<Task> actualNode = head;
        List<Task> tasks = new ArrayList<>();
        while (!(actualNode == null)) {

            tasks.add(actualNode.info);

            actualNode = actualNode.next;
        }

        return tasks;
    }

    class Node<Task> {
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

        if (!(node == null)) {
            final Node<Task> next = node.next;
            final Node<Task> prev = node.prev;
            node.info = null;

            if (head == node && taail == node) {
                head = null;

                taail = null;
            } else if (head == node && !(taail == node)) {
                head = next;
                head.prev = null;
            } else if (!(head == node) && taail == node) {
                taail = prev;
                taail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }

        }
    }
}


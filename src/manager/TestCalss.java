package manager;

import tasks.*;

import java.util.List;

public class TestCalss<T> extends InMemoryTaskManager {
    T t;
    public StringBuilder saveBuilder(List<T> listTask){
        StringBuilder STRING_BUILDER = new StringBuilder();
        //listTask = getTasks();
            if (listTask == getTasks()) {
                for (Task task : getTasks()) {
                    STRING_BUILDER.append(task.getId()).append(",");
                    STRING_BUILDER.append(task.getTypeOfTask()).append(",");
                    STRING_BUILDER.append(task.getTaskName()).append(",");
                    STRING_BUILDER.append(task.getStatus()).append(",");
                    STRING_BUILDER.append(task.getTaskDescription()).append("\n");

                }
            } else if(listTask == getEpics()) {
                for (Epic epic : getEpics()) {
                    STRING_BUILDER.append(epic.getId()).append(",");
                    STRING_BUILDER.append(epic.getTypeOfTask()).append(",");
                    STRING_BUILDER.append(epic.getTaskName()).append(",");
                    STRING_BUILDER.append(epic.getStatus()).append(",");
                    STRING_BUILDER.append(epic.getTaskDescription()).append("\n");
                }
            } else if (listTask == getSubtasks()) {
                for (Subtask subtask : getSubtasks()) {
                    STRING_BUILDER.append(subtask.getId()).append(",");
                    STRING_BUILDER.append(subtask.getTypeOfTask()).append(",");
                    STRING_BUILDER.append(subtask.getTaskName()).append(",");
                    STRING_BUILDER.append(subtask.getStatus()).append(",");

                }
            }
        return STRING_BUILDER;
    }
}

package tests.managerTests;

import exceptions.ManagerSaveException;
import manager.FileBackedTasksManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager>{
    File file = new File("data.csv");
    private FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
    @Test
    void test1_ShouldLoadWithEmptyListExceptions() throws ManagerSaveException, FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        pw.write("id,type,name,status,description,epic,startTime,duration" + "\n");
        pw.write("\n" + "HISTORY");
        pw.close();
        final NumberFormatException exception = assertThrows(
                NumberFormatException.class,
                () -> {
                    pw.write("id,type,name,status,description,epic,startTime,duration" + "\n");
                    pw.write("\n" + "HISTORY");
                    pw.close();
                    FileBackedTasksManager.loadFromFile(new File("data.csv"));
                }
        );
        assertEquals(exception.getMessage(),"For input string: \"HISTORY\"");
    }

    @Test
    void test2_ShouldLoadWithOutSubtask() throws FileNotFoundException {
        Task task = new Task("A", "A1", 1, TaskStatuses.NEW);
        Epic epic = new Epic("B", "B1", 2, TaskStatuses.IN_PROGRESS);

        fileBackedTasksManager.buildTask(task);
        fileBackedTasksManager.buildEpic(epic);
        fileBackedTasksManager.save();
        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(epic.getTaskName(), loadedManager.getEpics().get(0).getTaskName());
        assertEquals(task.getTaskName(), loadedManager.getTasks().get(0).getTaskName());

        assertEquals(fileBackedTasksManager.getTasks(), loadedManager.getTasks(),
                "Список задач после выгрузки не совпадает");
        assertEquals(fileBackedTasksManager.getEpics(), loadedManager.getEpics(),
                "Список эпиков после выгрузки не совпадает");
        assertEquals(fileBackedTasksManager.getPrioritizedTasks(), loadedManager.getPrioritizedTasks(),
                "Отсортированный список задач после выгрузки не совпадает");
        assertEquals(fileBackedTasksManager.getHistory(), loadedManager.getHistory(),
                "История после выгрузки не совпадает");
    }

    @Override
    FileBackedTasksManager createTaskManager() {
        return null;
    }
}
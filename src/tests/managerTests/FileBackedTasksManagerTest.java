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
        File file = new File("data.csv");
        PrintWriter pw = new PrintWriter(file);
        pw.write("id,type,name,status,description,epic" + "\n");
        pw.write("\n" + "HISTORY");
        pw.close();
        final NumberFormatException exception = assertThrows(
                NumberFormatException.class,
                () -> {
                    pw.write("id,type,name,status,description,epic" + "\n");
                    pw.write("\n" + "HISTORY");
                    pw.close();
                    FileBackedTasksManager.loadFromFile(new File("data.csv"));
                }
        );
        assertEquals(exception.getMessage(),"For input string: \"HISTORY\"");
    }

    @Test
    void test2_ShouldLoadWithOutSubtask() {
        Task task = new Task("A", "A1",1, TaskStatuses.NEW);
        Epic epic = new Epic("B", "B1", 2, TaskStatuses.IN_PROGRESS);
        fileBackedTasksManager.buildTask(task);
        fileBackedTasksManager.buildEpic(epic);
        fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("data.csv"));
        assertEquals(epic.getTaskName(), fileBackedTasksManager.receivingEpics(2).getTaskName());
        assertEquals(task.getTaskName(), fileBackedTasksManager.receivingTasks(1).getTaskName());
    }

    @Override
    FileBackedTasksManager createTaskManager() {
        return null;
    }
}
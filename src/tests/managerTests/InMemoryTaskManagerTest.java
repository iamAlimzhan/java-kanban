package tests.managerTests;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
 class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();    //Получение менеджера задач
    }

     @Override
     TaskManager createTaskManager() {
         return null;
     }
 }
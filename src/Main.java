import manager.*;
import server.HttpTaskServer;
import tasks.*;

import java.io.File;
import java.io.IOException;

import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new HttpTaskServer().start();
    }
}
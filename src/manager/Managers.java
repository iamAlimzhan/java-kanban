package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.InstantAdapter;

import java.time.Instant;

public class Managers {

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantAdapter());
        return gsonBuilder.create();
    }
}

package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class DurationAdapter extends TypeAdapter<Long> {
    //метод отвечает за сериализацию Long значения в строку JSON
    @Override
    public void write(JsonWriter jsonWriter, Long duration) throws IOException {
        if(duration != 0){
            jsonWriter.value(duration.toString());
        } else{
            jsonWriter.nullValue();
        }
    }
    //метод отвечает за десериализацию строки JSON обратно в Long
    @Override
    public Long read(JsonReader jsonReader) throws IOException {
        return Long.parseLong(jsonReader.nextString());
    }
}

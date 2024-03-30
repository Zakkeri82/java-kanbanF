package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;


public class HttpTaskServer {
     private static final int PORT = 8080;
     private static HttpServer httpServer;

     public HttpTaskServer(TaskManager taskManager) throws IOException {
          httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
          httpServer.createContext("/tasks", new TasksHandle(taskManager));
          httpServer.createContext("/epics", new EpicsHandle(taskManager));
          httpServer.createContext("/subtasks", new SubtasksHandle(taskManager));
          httpServer.createContext("/history", new HistoryHendler(taskManager));
          httpServer.createContext("/prioritized", new PrioritizedHandle(taskManager));
     }

     public void start() {
          System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
          httpServer.start();
     }

     public void stop() {
          httpServer.stop(0);
          System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
     }

     public static Gson getGson() {
          GsonBuilder gsonBuilder = new GsonBuilder();
          gsonBuilder.serializeNulls();
          gsonBuilder.setPrettyPrinting();
          gsonBuilder.serializeNulls();
          gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
          gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
          return gsonBuilder.create();
     }
}

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.taskManager.http.HttpTaskServer;
import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.InMemoryTaskManager;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
            .create();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTask();
        manager.deleteSubTask();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("починить машину", "важно", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(300), 5);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("починить машину", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Description 1", Status.NEW, LocalDateTime.now().plusMinutes(300), 5);
        Task task2 = new Task("Test 2", "Description 2", Status.DONE, LocalDateTime.now().plusMinutes(360), 5);

        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/task"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Description 1", Status.NEW, LocalDateTime.now().plusMinutes(310), 5);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/task?id=" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        Task retrievedTask = manager.getTaskById(task.getId());
        assertNotNull(retrievedTask, "Задача не найдена");
        assertEquals(task.getName(), retrievedTask.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {
        manager.addTask(new Task("Test 1", "Description 1", Status.NEW, LocalDateTime.now().plusMinutes(320), 5));
        manager.addTask(new Task("Test 2", "Description 2", Status.NEW, LocalDateTime.now().plusMinutes(330), 5));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/task"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        assertTrue(manager.getTasks().isEmpty(), "Задачи не были удалены");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        manager.addEpic(new Epic("Уборка!", "пора", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), 5));

        SubTask subtask = new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(2000), 5, 1);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        // Проверяем через менеджер
        List<SubTask> subtasks = manager.getSubTasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertEquals("Уборка в кухне", subtasks.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик3", "пора3", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(100), 5);
        manager.addEpic(epic);
        SubTask subtask1 = new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(20), 5, 1);
        SubTask subtask2 = new SubTask("Уборка в кухне2", "помыть посуду2", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(30), 5, 1);

        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtask"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<SubTask> subtasks = manager.getSubTasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "пора", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), 5);
        manager.addEpic(epic);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный код ответа");

        List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(2, epics.size(), "Некорректное количество эпиков");
        assertEquals("Эпик", epics.get(0).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Уборка!", "пора", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(40), 5);
        Epic epic2 = new Epic("Уборка!", "пора", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(50), 5);

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epic"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(2, epics.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task = new Task("починить машину", "важно", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(300), 5);
        manager.addTask(task);
        manager.getTaskById(task.getId()); // Добавляем в историю

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        List<Task> history = manager.getHistory();
        assertNotNull(history, "История не возвращается");
        assertEquals(1, history.size(), "Некорректное количество элементов в истории");
        assertEquals(task.getName(), history.get(0).getName(), "Некорректная задача в истории");
    }

    @Test
    public void testPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Description 1", Status.NEW, LocalDateTime.now().plusMinutes(200), 5);
        Task task2 = new Task("Test 2", "Description 2", Status.DONE, LocalDateTime.now().plusMinutes(260), 5);

        manager.addTask(task1);
        manager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный код ответа");

        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Приоритезированные задачи не возвращаются");
        assertEquals(2, prioritizedTasks.size(), "Некорректное количество приоритезированных задач");

    }
}
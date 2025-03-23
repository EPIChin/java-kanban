import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenegerTask {
    static int id = 0;
    static HashMap<Integer, Task> tasks = new HashMap<>();
    static HashMap<Integer, Epic> epics = new HashMap<>();
    static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public static void addTask(Task task) {
        tasks.put(id++, task);
    }

    public static void addEpic(Epic epic) {
        epics.put(id++, epic);
    }

    public static void addSubTask(SubTask subTask) {
        subTasks.put(id++, subTask);
        Epic.addSubTask(subTask);

        Epic epic = epics.get(subTask.getEpicId());
        epic.setStatus(SubTask.checkStatusEpic(subTask));
        epic.setId(subTask.getEpicId());
        updatepics(epic);
    }

    public static Object printTask() {
        return MenegerTask.tasks.toString();
    }

    public static Object printEpic() {
        return MenegerTask.epics.toString();
    }

    public static Object printSubTask() {
        return MenegerTask.subTasks.toString();
    }

    public static void deleteTask() {
        tasks.clear();
    }

    public static void deletepics() {
        epics.clear();
    }

    public static void deleteSubTask() {
        subTasks.clear();
    }

    public static Object getasksById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public static Object getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public static Object getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    public static void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else tasks.put(id++, task);
    }

    public static void updatepics(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else epics.put(id++, epic);
    }

    public static void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
        } else subTasks.put(id++, subTask);
    }

    public static void deleteTask(int id) {
        tasks.remove(id);
    }

    public static void deletepics(int id) {
        epics.remove(id);
    }

    public static void deleteSubTask(int id) {
        subTasks.remove(id);
    }

    public static Object getAllSubtasksOfEpic(int id) {
        epics.get(id);
        Epic epic = epics.get(id);
        List<Object> idSubTasks = new ArrayList<>();
        for (int i : epic.getIdSubTasks()) {
            idSubTasks.add(MenegerTask.getSubTaskById(i));
        }
        return idSubTasks;
    }
}

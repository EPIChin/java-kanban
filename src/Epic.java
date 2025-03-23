import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> idSubTasks = new ArrayList<>();

    public List<Integer> getIdSubTasks() {
        return idSubTasks;
    }

    public Epic(String name, String description) {
        super(name, description);
    }


    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public static void addSubTask(SubTask subTask) {
        int x = subTask.getEpicId();
        if (MenegerTask.epics.containsKey(x)) {
            Epic epic = MenegerTask.epics.get(x);
            epic.idSubTasks.add(MenegerTask.id - 1);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + idSubTasks +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
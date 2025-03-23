import java.util.List;

public class SubTask extends Task {
    public Integer getEpicId() {
        return epicId;
    }

    private Integer epicId;

    public SubTask(int id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    public static Status checkStatusEpic(SubTask subTask) {
        int EpicId = subTask.getEpicId();

        if (MenegerTask.epics.get(EpicId).getIdSubTasks().isEmpty() || isStatus(MenegerTask.epics.get(EpicId).getIdSubTasks(), Status.NEW)) {
            return Status.NEW;
        } else if (isStatus(MenegerTask.epics.get(EpicId).getIdSubTasks(), Status.DONE)) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    private static boolean isStatus(List<Integer> tasks, Status status) {
        for (Integer task : tasks) {
            if (MenegerTask.subTasks.get(task).getStatus() == status) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
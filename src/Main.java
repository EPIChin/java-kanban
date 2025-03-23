
public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        MenegerTask.addTask(new Task("починть машину", "важно"));
        MenegerTask.addTask(new Task("починть туалет", " Очень важно", Status.IN_PROGRESS));

        MenegerTask.addEpic(new Epic("Уборка!", "пора"));
        MenegerTask.addEpic(new Epic("Учеба", " важно"));

        MenegerTask.addSubTask(new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, 2));
        MenegerTask.addSubTask(new SubTask("Уборка на балконе", "Убрать велосипед", Status.IN_PROGRESS, 2));
        MenegerTask.addSubTask(new SubTask("Прочитать", "Книгу", Status.DONE, 3));

        MenegerTask.updateTask(new Task(1, "починить туалет", " Очень важно", Status.DONE));

        System.out.println(MenegerTask.printTask());
        System.out.println(MenegerTask.printEpic());
        System.out.println(MenegerTask.printSubTask());

        System.out.println(MenegerTask.getAllSubtasksOfEpic(2));

    }
}

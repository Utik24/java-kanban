import managers.InMemoryTaskManager;
import interfaces.TaskManager;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Покушать", "Приготовить покушать и поесть");
        Task task2 = new Task("Помыться", "Принять душ");
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.createTask(task2);


        SubTask subtask1 = new SubTask("Изучить теорию", "пройти теоритическую часть на практикуме");
        inMemoryTaskManager.createSubtask(subtask1);

        SubTask subtask2 = new SubTask("Написать программу", "Опираясь на теорию написать код ТЗ");
        inMemoryTaskManager.createSubtask(subtask2);

        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subtask1);
        subTaskList.add(subtask2);
        Epic epic1 = new Epic("Сделать ТЗ по проге", "реализовать трекер задач", subTaskList);
        inMemoryTaskManager.createEpic(epic1);
        Epic epic2 = new Epic("Выложить на гит ТЗ", "для проверки ТЗ необходимо выложить его на свой гит");
        inMemoryTaskManager.createEpic(epic2);

        SubTask subtask3 = new SubTask("Закоммитить и запушить", "делаем коммит и пушим наш проект на удаленный репозиторий");
        inMemoryTaskManager.createSubtask(subtask3);
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllEpics().get(0).getSubtasks());
        inMemoryTaskManager.removeAllSubTasks();
        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllEpics().get(0).getSubtasks());

        //суб таски меняются везде тк меняется сам объект субтаски и ссылка в памяти на этот объект и в хэшмапе и в листе в эпике одна поэтому и там и там субтаска поменяется
        //на этом куске кода видно что субтаска меняется везде при ее изменении
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(subtask2);
        subtask2.setStatus(Status.DONE);
        System.out.println(epic1.getSubtasks());
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        System.out.println(" ");
        System.out.println(" ");
        System.out.println(" ");

        System.out.println("Все задачи:");
        inMemoryTaskManager.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        inMemoryTaskManager.getAllEpics().forEach(System.out::println);

        System.out.println("\nПодзадачи эпика 1:");
        inMemoryTaskManager.getSubtasksByEpicId(epic1.getId()).forEach(System.out::println);

        System.out.println("\nПодзадачи эпика 2:");
        inMemoryTaskManager.getSubtasksByEpicId(epic2.getId()).forEach(System.out::println);

        task1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);

        System.out.println("\nОбновленные статусы:");
        System.out.println(task1);
        System.out.println(subtask2);
        System.out.println("Статус эпика 1 после изменения подзадачи: " + epic1.getStatus());
        System.out.println("Статус эпика 2: " + epic2.getStatus());

        inMemoryTaskManager.removeTaskById(task1.getId());
        inMemoryTaskManager.removeTaskById(epic2.getId());

        System.out.println("\nСписки после удаления по id:");
        System.out.println("Все задачи:");
        inMemoryTaskManager.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        inMemoryTaskManager.getAllEpics().forEach(System.out::println);
        System.out.println(inMemoryTaskManager.getHistory());
    }
}



public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Покушать", "Приготовить покушать и поесть", 0, Status.NEW);
        Task task2 = new Task("Помыться", "Принять душ", 0, Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Сделать ТЗ по проге", "реализовать трекер задач", 0);
        taskManager.createEpic(epic1);

        SubTask subtask1 = new SubTask("Изучить теорию", "пройти теоритическую часть на практикуме", 0, Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);

        SubTask subtask2 = new SubTask("Написать программу", "Опираясь на теорию написать код ТЗ", 0, Status.IN_PROGRESS, epic1.getId());
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Выложить на гит ТЗ", "для проверки ТЗ необходимо выложить его на свой гит", 0);
        taskManager.createEpic(epic2);

        SubTask subtask3 = new SubTask("Закоммитить и запушить", "делаем коммит и пушим наш проект на удаленный репозиторий", 0, Status.DONE, epic2.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Все задачи:");
        taskManager.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        taskManager.getAllEpics().forEach(System.out::println);

        System.out.println("\nПодзадачи эпика 1:");
        taskManager.getSubtasksByEpicId(epic1.getId()).forEach(System.out::println);

        System.out.println("\nПодзадачи эпика 2:");
        taskManager.getSubtasksByEpicId(epic2.getId()).forEach(System.out::println);

        task1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);

        System.out.println("\nОбновленные статусы:");
        System.out.println(task1);
        System.out.println(subtask2);
        System.out.println("Статус эпика 1 после изменения подзадачи: " + epic1.getStatus());
        System.out.println("Статус эпика 2: " + epic2.getStatus());

        taskManager.removeTaskById(task1.getId());
        taskManager.removeTaskById(epic2.getId());

        System.out.println("\nСписки после удаления по id:");
        System.out.println("Все задачи:");
        taskManager.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        taskManager.getAllEpics().forEach(System.out::println);
    }
}



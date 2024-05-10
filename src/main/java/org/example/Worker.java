package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.LinkedList;

public class Worker implements Runnable{
    public static LinkedList<Worker> listOfWorkers = new LinkedList<>(); //список всех работников

    private final String name;
    public final int id;
    private boolean status;
    private int workedHours; //часы работы
    private int idleHours; //часы простоя
    public LinkedList<Task> tasks; //список задач

    public static final int MAX_WORK_HOURS = 8;
    public static final int TIME_TO_SLEEP = 12;
    public static int WORKING_DAY = 1;
    private static final String TITLE_OF_TASK_WORKERS = "C:\\Users\\User\\Documents\\GitHub\\java-multithread-fewalthel\\src\\main\\java\\org\\example\\workers.xlsx";

    public Worker(String name, int id) {
        this.name = name;
        this.id = id;
        this.status = true;
        this.tasks = new LinkedList<>();
        this.workedHours = 0;
        this.idleHours = 0;
    }

    /**
     * Метод для начинания выполнения задачи
     */
    @Override
    public void run() {
        int initialCountOfTasks = this.tasks.size(); //изначальное количество задач
        System.out.println("Текущие задачи работника с айди "+this.id+":");
        this.tasks.forEach(System.out::println);

        while (!this.tasks.isEmpty()) { //пока задачи есть в списке
            //начинаем новый рабочий день
            //WORKING_DAY++;
            Task currentTask = this.tasks.getFirst();
            int hoursToWork = Math.min(currentTask.remainingHours, MAX_WORK_HOURS - this.workedHours);

            System.out.println("Задача №" + currentTask.number + " начала выполняться");
            while (currentTask.status) { //если задача ещё не выполнена, начинаем её выполнять
                this.workedHours += hoursToWork;
                currentTask.remainingHours -= hoursToWork;
                System.out.println(this.name + ": выполнено " + hoursToWork + " часов из задачи №" + currentTask.number);

                //если время выполнения задачи закончилось, значит мы выполнили задачу (на этом моменте происходит выход из цикла while)
                if (currentTask.remainingHours == 0) {
                    Task.changeTaskStatus(currentTask.number); //меняем статус задачи
                    this.tasks.removeFirst(); //удаляем задачу из списка
                    break;
                }

                //если работяга отработал 8-часовой рабочий день, пора и честь знать
                if (this.workedHours == MAX_WORK_HOURS) {
                    int counterOfDoneTasks = initialCountOfTasks-this.tasks.size(); //подводим итоги дня, сколько задач успел сделать работяга

                    //вносим данные о проделанной работе в течение дня в таблицу
                    addDataAboutWorkingDayInTable(counterOfDoneTasks, this.workedHours, this.id, WORKING_DAY);
                    System.out.println(this.name + ": закончил рабочий день. Время простоя: " + this.idleHours + " часов");

                    this.idleHours+= TIME_TO_SLEEP;
                    this.workedHours = 0;
                    //работник уходит домой баиньки на 12-часовой плотный сон
                    try {
                        Thread.sleep(TIME_TO_SLEEP);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.println(this.name + ": выполнил все задачи. Время на работе: " + (this.workedHours + this.idleHours) + " часов");
    }

    /**
     * Метод, для внесения данных в таблицу о прошедшем рабочем дне
     * @param couterOfDoneTasks количество проделанных задач
     * @param workingTime количество часов, проведённых на работе
     */
    public void addDataAboutWorkingDayInTable(int couterOfDoneTasks, int workingTime, int idWorker, int day) {
        System.out.println("данные успешно добавлены в таблицу для статистики");
    }

    /**
     * Метод, возвращающий работника по id
     * @param idWorker внутренний номер работника
     */
    public static Worker getWorker(int idWorker) {
        for (Worker worker: listOfWorkers) {
            if (worker.id == idWorker) {
                return worker;
            }
        }
        return null; // Работник не найден
    }

    /**
     * Метод для добавления нового работника в компанию(принятия на работу)
     * @param name имя нового работника
     * @param idWorker внутренний номер нового работника
     */
    public static void addWorker(String name, int idWorker) {
        if (!idInTable(idWorker)) {
            //содаем нового работника
            Worker worker = new Worker(name, idWorker);
            listOfWorkers.add(worker);
            //добавляем данные о нём в таблицу работников
            addWorkerOnTable(worker);
        } else {
            System.out.println("Работник с id "+idWorker+" уже есть в базе данных");
        }
    }

    /**
     * Метод для добавления данных о сотруднике в таблицу
     * @param worker работник, данные о котором надо добавить
     */
    private static void addWorkerOnTable(Worker worker) {
        String fileName = TITLE_OF_TASK_WORKERS;

        try (FileInputStream fis = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(fis); FileOutputStream fos = new FileOutputStream(fileName)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            // Добавляем данные работника в ячейки
            newRow.createCell(0).setCellValue(worker.name);
            newRow.createCell(1).setCellValue(worker.id);
            newRow.createCell(2).setCellValue(worker.status);

            workbook.write(fos); // Сохраняем изменения

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод, возвращаюий статус работника по Id
     * @param id внутренний номер работника
     */
    public static boolean workerIsWorks(int id) {
        String fileName = TITLE_OF_TASK_WORKERS;

        try (FileInputStream fis = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell searchCell = row.getCell(1); // ID во втором столбце (индекс 1)
                Cell statusCell = row.getCell(2); // статус работы в третьем столбце (индекс 2)

                if (searchCell != null && searchCell.getCellType() == CellType.NUMERIC && searchCell.getNumericCellValue() == id) {
                    if (statusCell != null) {
                        return statusCell.getBooleanCellValue();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // работник не найден или статус не "true"
    }

    /**
     * Метод, проверяющий, есть ли id работника в таблице
     * @param id внутренний номер работника
     */
    public static boolean idInTable(int id){
        String fileName = TITLE_OF_TASK_WORKERS;

        try (FileInputStream fis = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell searchCell = row.getCell(1);
                if (searchCell != null && searchCell.getCellType() == CellType.NUMERIC && searchCell.getNumericCellValue() == id) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
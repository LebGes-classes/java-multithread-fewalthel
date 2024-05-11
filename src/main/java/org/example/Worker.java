package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.LinkedList;

public class Worker implements Runnable{
    public static LinkedList<Worker> listOfWorkers = new LinkedList<>(); //список всех работников

    public final String name; //имя работника
    public final int id; //айди работника
    public boolean status; //статус работника (false, если работник уволен)
    public int workedHours; //часы работы
    public int idleHours; //часы простоя
    public LinkedList<Task> tasks; //список задач

    public static final int MAX_WORK_HOURS = 8; //
    public static final int TIME_TO_SLEEP = 12*1000; //время для сна (12 секунд)
    public static int WORKING_DAY = 1; //счётчик рабочих дней
    public static final String TITLE_OF_WORKERS_TABLE = "C:\\Users\\User\\Documents\\GitHub\\java-multithread-fewalthel\\src\\main\\java\\org\\example\\workers.xlsx";
    public static final String TITLE_OF_STATISTICS_TABLE = "C:\\Users\\User\\Documents\\GitHub\\java-multithread-fewalthel\\src\\main\\java\\org\\example\\statistics.xlsx";

    public Worker(String name, int id) {
        this.name = name;
        this.id = id;
        this.status = true;
        this.tasks = new LinkedList<>();
        this.workedHours = 0;
        this.idleHours = 0;
    }

    /**
     * Метод, Возвращающий изначльное количество задач сотрудника
     */
    public int getInitialCountOfTasks() {
        return this.tasks.size();
    }

    /**
     * Метод для имитации выполнения задач сотрудниками
     */
    @Override
    public void run() {
        int initialCountOfTasks = getInitialCountOfTasks();
        System.out.println("Текущие задачи работника с айди "+this.id+":");
        this.tasks.forEach(System.out::println);

        int counterOfDoneTasks = 0;
        while (!this.tasks.isEmpty()) { // работник работает, пока задачи есть в списке

            Task currentTask = this.tasks.getFirst(); //берем текущую задачу>

            System.out.println("Задача №" + currentTask.number + " начала выполняться");
            int hoursToWork = 0; // отработанные часы
            while (currentTask.status) { // делаем задачу, пока её статус "Не выполнено"
                this.workedHours += 1; // одна итерация - один час работы сотрудника
                currentTask.remainingHours -= 1; // один час задачи выполнен - уменьшаем её время исполнения на 1
                hoursToWork += 1; // прибавляем отработанные часы

                //делаем остановку на 1 секунду между рабочими часами
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println(this.name + ": выполнено " + hoursToWork + " часов из задачи №" + currentTask.number);

                // если время выполнения задачи закончилось, значит мы выполнили задачу (на этом моменте происходит выход из цикла while)
                if (currentTask.remainingHours == 0) {
                    Task.changeTaskStatus(currentTask.number); // меняем статус задачи
                    this.tasks.removeFirst(); // удаляем задачу из списка
                    System.out.println("Задача №" + currentTask.number + " выполнена");
                    counterOfDoneTasks += 1; // увеличиваем количество выполненных задач на 1
                    break;
                }

                // если работяга отработал 8-часовой рабочий день, пора и честь знать (завершаем рабочий день и идем спатки)
                if (this.workedHours == MAX_WORK_HOURS) {
                    // вносим данные о проделанной работе в течение дня в таблицу
                    addDataAboutWorkingDayInTable(counterOfDoneTasks, this.id, WORKING_DAY, initialCountOfTasks);
                    System.out.println(this.name + ": закончил рабочий день. Время простоя: " + this.idleHours/1000 + " часов");

                    counterOfDoneTasks = 0; // обнуляем счётчик выполненных задач в течение дня
                    this.idleHours+= TIME_TO_SLEEP; // к времени простоя сотрудника прибавляем время сна(то время, пока он не работает)
                    this.workedHours = 0; // обнуляем счётчик рабочего времени

                    // работник уходит домой баиньки на 12-часовой плотный сон
                    try {
                        Thread.sleep(TIME_TO_SLEEP);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    WORKING_DAY+=1; // прибавляем 1 отработанный день
                }
            }
        }

        System.out.println(this.name + ": выполнил все задачи.");
    }

    /**
     * Метод для внесения данных в таблицу о прошедшем рабочем дне
     * @param couterOfDoneTasks количество проделанных задач
     * @param day номер дня по счёту
     * @param idWorker внутренний номер работника, о котором собирается статистика
     * @param initialCountOfTasks изначальное количество задач работника
     */
    public void addDataAboutWorkingDayInTable(int couterOfDoneTasks, int idWorker, int day, int initialCountOfTasks) {
        String fileName = TITLE_OF_STATISTICS_TABLE;
        try (FileInputStream fis = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(fis); FileOutputStream fos = new FileOutputStream(fileName)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            // добавляем данные о прошедшем рабочем дне в ячейки
            newRow.createCell(0).setCellValue(idWorker);
            newRow.createCell(1).setCellValue(day);
            newRow.createCell(2).setCellValue(couterOfDoneTasks);

            //считаем производительность в процентах
            String proizvoditelnost = Double.toString((((double) couterOfDoneTasks) / (double) (initialCountOfTasks) )* 100) + "%";
            newRow.createCell(3).setCellValue(proizvoditelnost);

            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Данные успешно добавлены в таблицу для статистики.");
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
        return null; // работник не найден
    }

    /**
     * Метод для добавления нового работника в компанию(принятия на работу)
     * @param name имя нового работника
     * @param idWorker внутренний номер нового работника
     */
    public static void addWorker(String name, int idWorker) {
        if (!idInTable(idWorker)) {
            // содаем нового работника
            Worker worker = new Worker(name, idWorker);
            listOfWorkers.add(worker);
            addWorkerOnTable(worker); // добавляем данные о нём в таблицу работников
        } else {
            System.out.println("Работник с id "+idWorker+" уже есть в базе данных");
        }
    }

    /**
     * Метод для добавления данных о сотруднике в таблицу
     * @param worker работник, данные о котором надо добавить
     */
    public static void addWorkerOnTable(Worker worker) {
        String fileName = TITLE_OF_WORKERS_TABLE;

        try (FileInputStream fis = new FileInputStream(fileName); Workbook workbook = new XSSFWorkbook(fis); FileOutputStream fos = new FileOutputStream(fileName)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            // добавляем данные работника в ячейки
            newRow.createCell(0).setCellValue(worker.name);
            newRow.createCell(1).setCellValue(worker.id);
            newRow.createCell(2).setCellValue(worker.status);
            workbook.write(fos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод, возвращаюий статус работника по Id
     * @param id внутренний номер работника
     */
    public static boolean workerIsWorks(int id) {
        String fileName = TITLE_OF_WORKERS_TABLE;

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
        String fileName = TITLE_OF_WORKERS_TABLE;

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
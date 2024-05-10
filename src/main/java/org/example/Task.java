package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Scanner;

public class Task {
    public int number; //порядковый номер задачи
    private int idWorker; // айди исполнителя
    public int remainingHours; //время в часах, выделенное на исполнение задачи (>=1 &&<= 16)
    public boolean status; //статус задачи (false, если задача выполнена)
    private static int counterOfTasks = 1;

    private static final String TITLE_OF_TASK_TABLE = "C:\\Users\\User\\Documents\\GitHub\\java-multithread-fewalthel\\src\\main\\java\\org\\example\\tasks.xlsx";

    public Task(int idWorker) {
        //установка времени выполнения задачи
        boolean flag = true;
        System.out.println("Введите время, выделенное на выполнение задачи");
        while (flag) {
            Scanner scan = new Scanner(System.in);
            int time = scan.nextInt();
            if (time >= 1 && time <= 16) {
                this.remainingHours = time;
                flag = false;
            } else {
                System.out.println("Время, выделенное на выполнение задачи должно быть >=1 часа и <=16 часов.\n" +
                        "Попробуйте выбрать время заново:");
            }
        }
        this.status = true;
        this.number = counterOfTasks;
        this.idWorker = idWorker;
    }

    /**
     * Метод для добавления новой задачи сотруднику
     * @param idWorker внутренний номер исполнителя
     */
    public static void addTask(int idWorker) {
        if (Worker.idInTable(idWorker)) {
            if (Worker.workerIsWorks(idWorker)) {
                Task task = new Task(idWorker);
                if (Worker.getWorker(idWorker) != null) {
                    Worker.getWorker(idWorker).tasks.add(task);
                    addTaskOnTable(task);
                    counterOfTasks++;
                } else{
                    System.out.println("Работник с номером "+idWorker+" не найден");
                }
            } else {
                System.out.println("Работник с номером "+idWorker+" уволен");
            }
        } else {
            System.out.println("Работника с номером "+idWorker+" нет");
        }
    }

    /**
     * Метод для изменения статуса задачи
     * @param number номер задачи
     */
    public static void changeTaskStatus(int number) {
        String fileName = TITLE_OF_TASK_TABLE;

        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(fileName)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell searchCell = row.getCell(0); // номер задачи в первом столбце (индекс 0)
                Cell statusCell = row.getCell(3); // статус в четвертом столбце (индекс 3)

                if (searchCell != null && searchCell.getCellType() == CellType.NUMERIC && searchCell.getNumericCellValue() == number) {
                    if (statusCell != null ) {
                        statusCell.setCellValue(!statusCell.getBooleanCellValue());
                        break; // Выходим из цикла, так как задача найдена
                    }
                }
            }
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для добавления данных о задаче в таблицу
     * @param task задача, данные о которой необходимо добавить в таблицу
     */
    private static void addTaskOnTable(Task task) {
        // Путь к файлу Excel
        String fileName = TITLE_OF_TASK_TABLE;

        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(fileName)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row newRow = sheet.createRow(lastRowNum + 1);

            // Добавляем данные задачи в ячейки
            newRow.createCell(0).setCellValue(task.number);
            newRow.createCell(1).setCellValue(task.idWorker);
            newRow.createCell(2).setCellValue(task.remainingHours);
            newRow.createCell(3).setCellValue(task.status);

            workbook.write(fos); // Сохраняем изменения

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
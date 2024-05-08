package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.Thread;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.State.WAITING;


public class Task {
    private int number; //порядковый номер задачи
    private int idWorker; // айди исполнителя
    private int time; //время в часах, выделенное на исполнение задачи (>=1 &&<= 16)
    private int complete; //время, на сколько сделана задача (когда complete = time, статус задачи меняется)
    private boolean status; //статус задачи (false, если задача выполнена)
    private int counterOfTasks = 0;


    //private так как создать задачу без имполнителя мы не можем
    private Task(int id) {
        //установка времени выполнения задачи
        boolean flag = true;
        while (flag) {
            Scanner scan = new Scanner(System.in);
            int time = scan.nextInt();
            if (time >= 1 && time <= 16) {
                this.time = time;
                flag = false;
            } else {
                System.out.println();
            }
        }
        this.complete = 0;
        this.status = true;
        this.number = counterOfTasks;
        this.idWorker = id;
    }

    /**
     * Метод для добавления новой задачи сотруднику
     * @param idWorker внутренний номер исполнителя
     */
    public void addTask(int idWorker) throws Exception {
        if (Worker.idInTable(idWorker)) {
            if (Worker.workerIsWorks(idWorker)) {
                Task task = new Task(idWorker);
                addTaskOnTable(task);
                counterOfTasks++;
            } else {
                System.out.println("Работник уволен");
            }
        } else {
            System.out.println("Работника с таким id нет");
        }
    }

    /**
     * Метод для передачи задачи от одного исполнителя другому
     * @param idWorker внутренний номер нового исполнителя
     * @param number номер передаваемой задачи
     */
    public void TransmitTask(int number, int idWorker) throws IOException {
        //меняем исполнителя для задачи, при этом complete не изменяется
        String fileName = "/.tasks.xlsx";
        // Открытие файла Excel
        FileInputStream fis = new FileInputStream(fileName);
        Workbook workbook = new XSSFWorkbook(fis);
        FileOutputStream fot = new FileOutputStream(fileName);
        try {
            // Получение первого листа
            Sheet sheet = workbook.getSheetAt(0);
            // Итерация по строкам листа
            for (Row row : sheet) {
                // Получение ячейки из столбца поиска
                Cell searchCell = row.getCell(0);

                // Проверка, содержит ли ячейка искомое значение
                if (searchCell.getStringCellValue().equals(Integer.toString(number))) {
                    Cell modifyCell = row.getCell(1);
                    modifyCell.setCellValue(idWorker);
                    break;
                }
            }
            workbook.write(fot);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Закрытие потоков
            fis.close();
            fot.close();
            workbook.close();
        }
    }

    /**
     * Метод для начинания выполнения задачи
     * @param number номер задачи
     */
    public void DoTask(int number) throws InterruptedException, IOException {
        //если задача ещё не выполнена
        if (status) {
            if (complete == 0 ) { //если задачу еще не начинали выплнять, создаём для неё новый поток
                Thread thread = new Thread(
                        //если задача выполнена, меняем статус и останавливаем поток
                    if (complete == time) {
                        changeStatus(number);
                    //thread.stop();}); остановка жизненного цикла потока

                //если задача не выполнена, начинаем работу над ней
                System.out.println("Работу работаем..."););
                thread.start();
                Timer timer = new Timer();

                if (timer.getPassTime() == 8*3600000) { //преобразование мс в часы
                    thread.wait();
                }
            } else {
                if (Thread.currentThread().getState() == WAITING ) {
                    Thread.currentThread().notify();
                }
            }
        } else {
            System.out.println("Задача уже выполнена");
        }
    }

    public void StopTask

    /**
     * Метод для изменения статуса задачи
     * @param number номер задачи
     */
    public void changeStatus (int number) throws IOException{
        String fileName = "/.tasks.xlsx";
        // Открытие файла Excel
        FileInputStream fis = new FileInputStream(fileName);
        Workbook workbook = new XSSFWorkbook(fis);
        FileOutputStream fot = new FileOutputStream(fileName);
        try {
            // Получение первого листа
            Sheet sheet = workbook.getSheetAt(0);
            // Итерация по строкам листа
            for (Row row : sheet) {
                // Получение ячейки из столбца поиска
                Cell searchCell = row.getCell(0);

                // Проверка, содержит ли ячейка искомое значение
                if (searchCell.getStringCellValue().equals(Integer.toString(number))) {
                    Cell modifyCell = row.getCell(4);
                    // Получение ячейки для изменения
                    if (modifyCell.getStringCellValue().equals("true")) {
                        modifyCell.setCellValue("false");
                    } else {
                        modifyCell.setCellValue("true");
                    }
                    break;
                }
            }
            workbook.write(fot);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Закрытие потоков
            fis.close();
            fot.close();
            workbook.close();
        }
    }

    /**
     * Метод для добавления данных о задаче в таблицу
     * @param task задача, данные о которой необходимо добавить в таблицу
     */
    private void addTaskOnTable(Task task) throws IOException{
        // Путь к файлу Excel
        String fileName = "./tasks.xlsx";

        // Данные для добавления
        List<Object> newData = new ArrayList<>();
        newData.add(task.number);
        newData.add(task.idWorker);
        newData.add(task.time);
        newData.add(task.complete);

        // Открытие файла Excel
        FileInputStream fis = new FileInputStream(fileName);
        Workbook workbook = new XSSFWorkbook(fis);

        try {
            // Получение первого листа
            Sheet sheet = workbook.getSheetAt(0);

            // Получение номера последней строки
            int lastRowNum = sheet.getLastRowNum();

            // Создание новой строки
            Row newRow = sheet.createRow(lastRowNum + 1);

            // Добавление данных в новую строку
            for (int i = 0; i < newData.size(); i++) {
                Cell cell = newRow.createCell(i);
                cell.setCellValue((String) newData.get(i));
            }

            // Сохранение изменений
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            workbook.write(fileOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fis.close();
            workbook.close();
        }
    }
}
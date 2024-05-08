package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


public class Worker {
    private final String name;
    private final int id;
    private boolean status;

    private Worker(String name, int id) {
        this.name = name;
        this.id = id;
        this.status = true;
    }

    /**
     * Метод для добавления нового работника в компанию(принятия на работу)
     * @param name имя нового работника
     * @param id внутренний номер нового работника
     */
    public void addWorker(String name, int id) throws IOException{
        if (!idInTable(id)) {
            //содаем нового работника
            Worker worker = new Worker(name, id);
            //добавляем данные о нём в таблицу работников
            addWorkerOnTable(worker);
        } else {
            System.out.println("Данные о работнике уже есть в базе данных");
        }
    }

    /**
     * Метод для увольнения сотрудника из компании
     * @param id внутренний номер работника
     */
    public void removeWorker(int id) throws IOException {
        if (workerIsWorks(id)) {
            changeStatus(id);
        } else {
            System.out.println("Работник уже уволен");
        }
    }

    /**
     * Метод для возращения уже работавшего сотрудника в компанию
     * @param id внутренний номер работника
     */
    public void returnWorker(int id) throws IOException {
        if (!workerIsWorks(id)) {
            changeStatus(id);
        } else {
            System.out.println("Работник уже работает");
        }
    }

    /**
     * Метод для добавления данных о сотруднике в таблицу
     * @param worker работник, данные о котором надо добавить
     */
    private void addWorkerOnTable(Worker worker) throws IOException {
        // Путь к файлу Excel
        String fileName = "workers.xlsx";

        // Данные для добавления
        List<Object> newData = new ArrayList<>();
        newData.add(worker.name);
        newData.add(worker.id);
        newData.add(worker.status);

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

    /**
     * Метод, меняющий статус работника в таблице на противоположный
     * @param id внутренний номер работника
     */
    private void changeStatus(int id) throws IOException {
        String fileName = "/.workers.xlsx";
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
                Cell searchCell = row.getCell(1);

                // Проверка, содержит ли ячейка искомое значение
                if (searchCell.getStringCellValue().equals(Integer.toString(id))) {
                    Cell modifyCell = row.getCell(2);
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
     * Метод, возвращаюий статус работника по Id
     * @param id внутренний номер работника
     */
    public static boolean workerIsWorks(int id) throws IOException{
        String fileName = "/.workers.xlsx";

        // Открытие файла Excel
        FileInputStream fis = new FileInputStream(fileName);
        Workbook workbook = new XSSFWorkbook(fis);

        // Получение первого листа
        Sheet sheet = workbook.getSheetAt(0);

        // Итерация по строкам листа
        for (Row row : sheet) {
            // Получение ячейки из столбца поиска
            Cell cell = row.getCell(2);
            // Проверка, содержит ли ячейка искомое значение
            if (cell.getStringCellValue().equals(Integer.toString(id))) {
                // Получение ячейки для изменения
                if (cell.getStringCellValue().equals("true")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        // Закрытие потоков
        fis.close();
        workbook.close();
        return false;
    }

    /**
     * Метод, проверяющий, есть ли id работника в таблице
     * @param id данные, наличие которых необходимо проверить в таблице
     */
    public static boolean idInTable(int id) throws IOException {

        String fileName = "./workers.xlsx";
        FileInputStream fis = new FileInputStream(fileName);

        try {
            // Открытие файла Excel
            Workbook workbook = new XSSFWorkbook(fis);
            // Получение первого листа
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                Cell searchCell = row.getCell(1);
                if (searchCell.getStringCellValue().equals(Integer.toString(id))) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fis.close();
            return false;
        }
    }
}
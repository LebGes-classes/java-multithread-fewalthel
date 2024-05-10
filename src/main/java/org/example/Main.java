package org.example;

import java.util.Scanner;

import static org.example.Worker.*;
import static org.example.Task.*;

public class Main {

    public static void main(String[] args) throws Exception {
//        boolean flag = true;
//        while (flag) {
//            Scanner scan = new Scanner(System.in);
//            String answer = scan.nextLine();
//            switch (answer) {
//                //TODO: дописать функционал программы
//            }
//        }
        // Создаем список сотрудников
        addWorker("Маша", 123);
        addWorker("Саша", 14);


        //назначаем задачи для сотрудников
        addTask(14);
        addTask(14);
        addTask(123);


        // Запускаем потоки для каждого сотрудника
        for (Worker employee : listOfWorkers) {
            new Thread(employee).start();
        }

    }

    enum WORKERS {
        ADD_WORKER,
        REMOVE_WORKER,
        RETURN_WORKER,
        SHOW_STATISTICS, //увидеть статистику по работникам
    }

    enum TASKS {
        ADD_TASK, //добавить новую задачу
        TRANSMIT_TASK, //передать задачу
        SHOW_ACTUAL_TASKS, //увидеть актуальные задачи
    }

    enum ACTIONS {
        NEW_DAY, //начать новый рабочий день
        STOP //закончить работу программы
    }
}

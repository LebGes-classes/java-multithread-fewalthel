package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean flag = true;
        while (flag) {
            Scanner scan = new Scanner(System.in);
            String answer = scan.nextLine();
            switch (answer) {

            }
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

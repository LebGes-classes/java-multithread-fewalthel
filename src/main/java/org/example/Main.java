package org.example;

import static org.example.Worker.*;
import static org.example.Task.*;

public class Main {

    public static void main(String[] args) {

        // создаем сотрудников
        addWorker("Маша", 123);
        addWorker("Саша", 167);


        // назначаем задачи для сотрудников
        addTask(167);
        addTask(167);
        addTask(123);


        // запускаем потоки для каждого сотрудника
        for (Worker executor : listOfWorkers) {
            new Thread(executor).start();
        }

    }
}

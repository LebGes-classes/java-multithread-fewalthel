package org.example;

public class Timer {
    private long passtime;
    private long startTime; //начальное время
    private long finalTime; //конечное время

    //создаем таймер и сразу же запускаем его
    public Timer(){this.startTime = System.nanoTime();}

    /**
     * Метод, возвращающий прошедшее время в мс с начала последнего запуска таймера
     */
    public long getPassTime() {
        return System.nanoTime() - this.startTime;
    }
}
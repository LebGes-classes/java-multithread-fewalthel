import org.example.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.example.Worker.listOfWorkers;

public class WorkerTest {
    public Task TaskTest;
    public Worker WorkerTest;

    @Before
    public void setUp() {
        // Инициализация worker и task для тестирования
        WorkerTest = new Worker("тест", 555);
        TaskTest = new Task(WorkerTest.id);
    }

    @Test
    public void testAddWorker() {
        // проверяем, что работник добавляется в список всех работников
        assertTrue(listOfWorkers.contains(WorkerTest));
    }
}
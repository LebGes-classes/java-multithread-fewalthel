import org.example.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TaskTest {
    public Task TaskTest;
    public Worker WorkerTest;

    @Before
    public void setUp() {
        // Инициализация worker и task для тестирования
        WorkerTest = new Worker("тест", 345);
        TaskTest = new Task(WorkerTest.id);
    }

    @Test
    public void testAddTask() {
        // проверяем, что задача добавляется в список задач работника
        assertTrue(WorkerTest.tasks.contains(TaskTest));
    }

    @Test
    public void testChangeTaskStatus() {
        // проверяем, что статус задачи изменился
        TaskTest.changeTaskStatus(1);
        assertFalse(TaskTest.status);
    }
}
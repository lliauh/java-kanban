package test;

import org.junit.jupiter.api.BeforeEach;
import taskmanager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void createManager() {
        taskManager = new InMemoryTaskManager();
    }
}
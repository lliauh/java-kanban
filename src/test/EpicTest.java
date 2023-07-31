package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import taskmanager.Managers;
import taskmanager.TaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    private TaskManager taskManager;
    private Integer epic1Id;
    private Integer subtask1Id;
    private Integer subtask2Id;
    private Integer subtask3Id;


    @BeforeEach
    public void createManagerAndEpic() {
        taskManager = Managers.getDefault();
        epic1Id = taskManager.createEpic(new Epic("Epic1_title", "Epic1_description"));
    }

    private void createSubtasks() {
        subtask1Id = taskManager.createSubtask(new Subtask("Subtask1_title", "Subtask1_description",
                LocalDateTime.parse("2023-07-30T00:21:21"), 20), epic1Id);
        subtask2Id = taskManager.createSubtask(new Subtask("Subtask2_title", "Subtask2_description",
                LocalDateTime.parse("2023-07-30T01:21:21"), 20), epic1Id);
        subtask3Id = taskManager.createSubtask(new Subtask("Subtask3_title", "Subtask3_description",
                LocalDateTime.parse("2023-07-30T02:21:21"), 10), epic1Id);
    }

    @Test
    public void emptySubtasksStatusShouldBeNew() {
        Assertions.assertEquals(Status.NEW, taskManager.getEpicById(epic1Id).getStatus());
    }

    @Test
    public void allSubtasksAreNewStatusShouldBeNew() {
        createSubtasks();
        assertEquals(Status.NEW, taskManager.getEpicById(epic1Id).getStatus());
    }

    @Test
    public void allSubtasksAreDoneStatusShouldBeDone() {
        createSubtasks();
        taskManager.getSubtaskById(subtask1Id).setStatus(Status.DONE);
        taskManager.getSubtaskById(subtask2Id).setStatus(Status.DONE);
        taskManager.getSubtaskById(subtask3Id).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask1Id));
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask2Id));
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask3Id));

        assertEquals(Status.DONE, taskManager.getEpicById(epic1Id).getStatus());
    }

    @Test
    public void subtasksAreNewAndDoneStatusShouldBeInProgress() {
        createSubtasks();
        taskManager.getSubtaskById(subtask1Id).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask1Id));
        taskManager.getSubtaskById(subtask2Id).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask2Id));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1Id).getStatus());
    }

    @Test
    public void allSubtasksAreInProgressStatusShouldBeInProgress() {
        createSubtasks();
        taskManager.getSubtaskById(subtask1Id).setStatus(Status.IN_PROGRESS);
        taskManager.getSubtaskById(subtask2Id).setStatus(Status.IN_PROGRESS);
        taskManager.getSubtaskById(subtask3Id).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask1Id));
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask2Id));
        taskManager.updateSubtask(taskManager.getSubtaskById(subtask3Id));

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1Id).getStatus());
    }

    @Test
    public void epicDurationShouldBe50andStartEndTimeTest() {
        createSubtasks();
        assertEquals(50, taskManager.getEpicById(epic1Id).getDuration());
        assertEquals(LocalDateTime.parse("2023-07-30T00:21:21"), taskManager.getEpicById(epic1Id).getStartTime());
        assertEquals(LocalDateTime.parse("2023-07-30T02:31:21"), taskManager.getEpicById(epic1Id).getEndTime());
        assertEquals(LocalDateTime.parse("2023-07-30T01:21:21"),
                taskManager.getSubtaskById(subtask2Id).getStartTime());
        assertEquals(LocalDateTime.parse("2023-07-30T01:41:21"),
                taskManager.getSubtaskById(subtask2Id).getEndTime());
    }
}

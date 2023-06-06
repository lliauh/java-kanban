package taskmanager;

import task.*;

import java.util.Arrays;
import java.util.List;

public class Formatter {
    protected static String toString(Task task) {
        if (task.getType() == Type.SUBTASK) {
            return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + "," + task.getEpicId();
        } else {
            return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + ","
                    + task.getDescription() + ",";
        }
    }

    protected static Task fromString(String value) {
        String[] split = value.split(",");
        switch (split[1]) {
            case "TASK":
                Task task = new Task(split[2], split[4]);
                task.setId(Integer.parseInt(split[0]));
                task.setStatus(extractStatusFromString(split[3]));
                return task;
            case "EPIC":
                Epic epic = new Epic(split[2], split[4]);
                epic.setId(Integer.parseInt(split[0]));
                epic.setStatus(extractStatusFromString(split[3]));
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(split[2], split[4]);
                subtask.setId(Integer.parseInt(split[0]));
                subtask.setStatus(extractStatusFromString(split[3]));
                subtask.setEpicId(Integer.parseInt(split[5]));
                return subtask;
        }

        return null;
    }

    private static Status extractStatusFromString(String status) {
        if (status.equals("IN_PROGRESS")) {
            return Status.IN_PROGRESS;
        } else if (status.equals("DONE")) {
            return Status.DONE;
        } else {
            return Status.NEW;
        }
    }

    protected static String historyToString(HistoryManager manager) {
        List<Task> historyList = manager.getHistory();
        String[] historyIds = new String[historyList.size()];

        int counter = 0;
        for (Task task : historyList) {
            historyIds[counter] = Integer.toString(task.getId());
            counter++;
        }

        return String.join(",", historyIds);
    }

    protected static List<Integer> historyFromString(String value) {
        String[] splitted = value.split(",");
        Integer[] splittedInt = new Integer[splitted.length];

        for (int i = 0; i < splitted.length; i++) {
            splittedInt[i] = Integer.valueOf(splitted[i]);
        }

        return Arrays.asList(splittedInt);
    }
}

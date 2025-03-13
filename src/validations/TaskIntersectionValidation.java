package validations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TaskIntersectionValidation {
    private static final int INTERVALS_PER_DAY = 96; // 24 * 4 (по 15 минут)

    private Map<Integer, Boolean> schedule = new HashMap<>();

    private int getIntervalIndex(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int dayOfYear = dateTime.getDayOfYear() - 1; // Начинаем с 0
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int interval = (hour * 60 + minute) / 15;

        return (year * 365 + dayOfYear) * INTERVALS_PER_DAY + interval;
    }

    public boolean addTask(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);

        for (int i = getIntervalIndex(start); i <= getIntervalIndex(end); i++) {
            if (schedule.containsKey(i) && schedule.get(i)) {
                return false;
            }
        }

        for (int i = getIntervalIndex(start); i <= getIntervalIndex(end); i++) {
            schedule.put(i, true);
        }

        return true;
    }

    public boolean updateTask(LocalDateTime oldStart, Duration oldDuration, LocalDateTime newStart, Duration newDuration) {
        removeTask(oldStart, oldDuration);

        return addTask(newStart, newDuration);
    }

    public void removeTask(LocalDateTime start, Duration duration) {
        LocalDateTime end = start.plus(duration);
        for (int i = getIntervalIndex(start); i <= getIntervalIndex(end); i++) {
            schedule.remove(i);
        }
    }
}

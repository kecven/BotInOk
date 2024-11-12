package digital.moveto.botinok.model.utils;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BotinokUtils {

    public static boolean checkShabatDay() {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        DayOfWeek dayOfWeek = localDateTimeNow.getDayOfWeek();

        return dayOfWeek == DayOfWeek.FRIDAY && localDateTimeNow.getHour() >= 16
                || dayOfWeek == DayOfWeek.SATURDAY && localDateTimeNow.getHour() <= 21;
    }


    public static boolean equalsDateAndDateTime(LocalDate date, LocalDateTime dateTime){
        return date.getYear() == dateTime.getYear()
                && date.getMonthValue() == dateTime.getMonthValue()
                && date.getDayOfMonth() == dateTime.getDayOfMonth();
    }
}

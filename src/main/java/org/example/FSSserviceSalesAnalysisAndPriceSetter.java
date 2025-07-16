package org.example;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class FSSserviceSalesAnalysisAndPriceSetter {
    public static void main( String[] args ) {

        // Получаем текущее время
        LocalTime currentTime = LocalTime.now();
        System.out.println("Текущее время: " + currentTime);

        // Минута, с которой будем сравнивать
        int minuteToCompare = 30;

        // Получаем текущую минуту
        int currentMinute = currentTime.getMinute();

        // Сравниваем текущую минуту с заданной минутой
        if (currentMinute == minuteToCompare) {
            System.out.println("Текущая минута совпадает с заданной минутой.");
        } else {
            System.out.println("Текущая минута не совпадает с заданной минутой.");
        }

        // Если нужно сравнить с точностью до минуты (например, 14:30)
        LocalTime specificTime = LocalTime.of(currentTime.getHour(), minuteToCompare);

        // Сравниваем текущее время с заданным временем
        long minutesDifference = ChronoUnit.MINUTES.between(currentTime, specificTime);
        if (minutesDifference == 0) {
            System.out.println("Текущее время совпадает с заданным временем.");
        } else {
            System.out.println("Текущее время не совпадает с заданным временем. Разница в минутах: " + Math.abs(minutesDifference));
        }

//        Update update = new Update();
//        update.start();

    }
}

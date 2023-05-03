package com.akvelon.bitbuckler.extension

import java.time.Month

fun Month.toRussian(): String = when (this) {
    Month.JANUARY -> "Январь"
    Month.FEBRUARY -> "Февраль"
    Month.MARCH -> "Март"
    Month.APRIL -> "Апрель"
    Month.MAY -> "Май"
    Month.JUNE -> "Июнь"
    Month.JULY -> "Июль"
    Month.AUGUST -> "Август"
    Month.SEPTEMBER -> "Сентябрь"
    Month.OCTOBER -> "Октябрь"
    Month.NOVEMBER -> "Ноябрь"
    Month.DECEMBER -> "Декабрь"
}

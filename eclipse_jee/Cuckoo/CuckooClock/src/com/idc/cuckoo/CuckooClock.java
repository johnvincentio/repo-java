package com.idc.cuckoo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CuckooClock {
    public static void main(String args[])  {

        /*
         * calculate delay until next tick is due, round up to next quarter of an hour.
         */
        Calendar calendar = new GregorianCalendar();
        printCalendar (calendar);

        int minutes = calendar.get (Calendar.MINUTE);
        minutes = (minutes + 15) - ((minutes + 15) % 15);
        System.out.println("minutes "+minutes);
//        if (minutes >= 60)  minutes = 0;
        calendar.set (Calendar.MINUTE, minutes);
        calendar.clear (Calendar.SECOND);
        calendar.clear (Calendar.MILLISECOND);
        printCalendar (calendar);

        /*
         * calculate number of ticks refering to the next quarter hour.
         */
        int hours = calendar.get (Calendar.HOUR_OF_DAY);
        int ticks = hours * 4  + minutes / 15;
        System.out.println("ticks "+ticks+" hours "+hours+" minutes "+minutes);

        /*
         * calculate the delay until the next quarter of an hour
         */
        long current = System.currentTimeMillis();
        long delay = calendar.getTimeInMillis() - current;
        System.out.println("current "+current+" delay "+delay);

        /*
         * start the timer, delayed  until the next quarter hour, executes every quarter of an hour.
         */
        java.util.Timer timer = new java.util.Timer();
        timer.schedule (new Task(ticks), delay, 15 * 1000L * 60);
    }

    private  static void printCalendar (Calendar  calendar)  {
        SimpleDateFormat sdf = new SimpleDateFormat ("dd MMM yyyy hh:mm:ssss aaa");
        String date = sdf.format (calendar.getTime());
        System.out.println (date+" millis "+calendar.getTimeInMillis());
    }
 }

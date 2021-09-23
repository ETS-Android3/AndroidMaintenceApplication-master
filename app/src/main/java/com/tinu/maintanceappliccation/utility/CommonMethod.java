package com.tinu.maintanceappliccation.utility;

public class CommonMethod {

    public static String dateFormatInJapanease(String month,String day,String amOrPm,String year,String hour,String minitus)
    {
        String date;
        String yearr = year.substring(year.length() -2);

        if (amOrPm.equalsIgnoreCase("午前")||amOrPm.equalsIgnoreCase("AM"))
        {
            date=month+"月"+day+"日"+"午前"+hour+"時"+minitus+"分";

        }
        else {
            date=month+"月"+day+"日"+"午後"+hour+"時"+minitus+"分";
        }
        return date;
    }

    public static String timeFormatInJapanease(String amOrPm,String hour,String minitus)
    {
        String date;


        if (amOrPm.equalsIgnoreCase("午前")||amOrPm.equalsIgnoreCase("AM"))
        {

                date="午前"+hour+"時"+minitus+"分";



        }
        else {

                date="午後"+hour+"時"+minitus+"分";


        }
        date=date.replaceAll("am","");
        return date;
    }

    public static String timeFormatInJapaneaseAvoidZero(String amOrPm,String hour,String minitus)
    {
        String date;

        if (amOrPm.equalsIgnoreCase("午前")||amOrPm.equalsIgnoreCase("AM"))
        {
            if (minitus.equalsIgnoreCase("00"))
            {
                date="午前"+hour+"時";
            }
            else{
                date="午前"+hour+"時"+minitus+"分";
            }


        }
        else {
            if (minitus.equalsIgnoreCase("00"))
            {
                date="午後"+hour+"時";
            }
            else{
                date="午後"+hour+"時"+minitus+"分";
            }

        }
        return date;
    }

    public static String timeFormatInEnglish(String amOrPm,String hour,String minitus)
    {
        String date;

        if (amOrPm.equalsIgnoreCase("AM"))
        {
            date=hour+":"+minitus+"AM";

        }
        else {
            date=hour+":"+minitus+"PM";
        }
        return date;
    }
}

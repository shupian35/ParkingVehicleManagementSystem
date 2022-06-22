package com.shupian.pvms.utils;

import java.math.BigDecimal;

public class MoneyUtil {
    private static double month = 0.95;
    private static double query = 0.90;
    private static double year = 0.85;

    public static BigDecimal Money(Long time, String carTag){

        if(time<30L){
            return BigDecimal.valueOf(0);
        }
        if(time<60L){
            if(carTag.equals("1")){
                return BigDecimal.valueOf(5*month);
            }
            if (carTag.equals("2")){
                return BigDecimal.valueOf(5*query);
            }
            if (carTag.equals("3")){
                return BigDecimal.valueOf(5*year);
            }
            return BigDecimal.valueOf(5);
        }
        int hours = (int)(time/60.0 + 0.5);

        if (carTag.equals("1")) {
            return BigDecimal.valueOf((5 +(hours-1)*2) * month);
        }
        if (carTag.equals("2")) {
            return BigDecimal.valueOf((5 + (hours - 1) * 2) * query);
        }
        if (carTag.equals("3")) {
            return BigDecimal.valueOf((5 + (hours - 1) * 2) * year);
        }
        return BigDecimal.valueOf(5 + (hours - 1) * 2);


    }


    public static BigDecimal Money(long time) {

        if (time < 30L) {
            return BigDecimal.valueOf(0);
        }
        if (time < 60L) {

            return BigDecimal.valueOf(5);
        }
        int hours = (int) (time / 60.0 + 0.5);


        return BigDecimal.valueOf(5 + (hours - 1) * 2);
    }
}

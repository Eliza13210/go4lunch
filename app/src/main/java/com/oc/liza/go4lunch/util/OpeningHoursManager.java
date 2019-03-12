package com.oc.liza.go4lunch.util;

import android.util.Log;
import android.widget.TextView;

import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.models.OpeningHours;
import com.oc.liza.go4lunch.models.RestaurantDetails;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OpeningHoursManager {

    public int localTime;
    public int day;
    private List<OpeningHours> openHours;
    private RestaurantDetails details;
    List<OpeningHours> openingTime = new ArrayList<>();
    List<OpeningHours> closeTime = new ArrayList<>();
    String openMorning = "";
    String closingLunch = "";

    TextView opening_hours;

    public OpeningHoursManager(RestaurantDetails details, TextView opening_hours) {
        this.details = details;
        this.opening_hours = opening_hours;
    }

    public OpeningHoursManager() {

    }

    public void checkOpening() {

        getActualTimeAndDay();
        getOpeningHoursToday();

    }

    public void getOpeningHoursToday() {
        //Get list of opening hours for the restaurant
        openHours = details.getOpening_hours().getPeriods();

        int dayOfWeek = 0;

        //Check which day it is and set dayOfWeek to get the right opening hours
        switch (day) {
            case Calendar.SUNDAY:
                // Current day is Sunday
                dayOfWeek = 0;
                break;
            case Calendar.MONDAY:
                // Current day is Monday
                dayOfWeek = 1;
                break;
            case Calendar.TUESDAY:
                dayOfWeek = 2;
                // etc.
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = 3;
                break;
            case Calendar.THURSDAY:
                dayOfWeek = 4;
                break;
            case Calendar.FRIDAY:
                dayOfWeek = 5;
                break;
            case Calendar.SATURDAY:
                dayOfWeek = 6;
                break;
        }

        for (int i = 0; i < openHours.size(); i++) {
            //Find the corresponding open hours object in list depending on weekday
            if (openHours.get(i).getOpen().getDay() == dayOfWeek) {

                openMorning = openHours.get(i).getOpen().getTime();
                closingLunch = openHours.get(i).getClose().getTime();
Log.e("open", "open time"  + openMorning + "close" + closingLunch + " localtime " +localTime);
                int closingLunchInt = Integer.parseInt(closingLunch);
                int openMorningInt = Integer.parseInt(openMorning);

                //If restaurant is open
                //If restaurant is closing in 30 minutes
                if ((localTime - closingLunchInt) < 30 && (localTime - closingLunchInt) >0) {
                    opening_hours.setText("Closing soon");
                }
                //If restaurant is closed
                else if (localTime > closingLunchInt) {
                    opening_hours.setText("Closed");
                    Log.e("closed", " closed " + localTime + " " + closingLunchInt);
                }
                //If restaurant is not yet open for lunch
                else if (localTime < openMorningInt) {

                    Log.e("closed", " closed " + localTime + " " + openMorningInt);
                    String str = Integer.toString(openMorningInt);
                    str = new StringBuilder(str).insert(str.length()-2, ".").toString();
                   // String isOpening = String.valueOf(openMorningInt);
                    opening_hours.setText("Opens at " + str + "pm");
                } else if (localTime < closingLunchInt) {

                    String str = Integer.toString(closingLunchInt);

                    str = new StringBuilder(str).insert(str.length()-2, ".").toString();
                    Log.e("stringbuilder", "string " +str);
                    // String isClosing = String.valueOf(closingLunchInt);
                    opening_hours.setText("Open until " + str + "pm");
                }else if(details.getOpening_hours().getOpen_now()) {
                    opening_hours.setText("Open");

                    Log.e("open", " nothing matched " + localTime + " " + closingLunchInt + openMorningInt);
                }else if(!details.getOpening_hours().getOpen_now()){
                    opening_hours.setText("Closed");
                    Log.e("open", " nothing matched " + localTime + " " + closingLunchInt + openMorningInt);

                }
            }
        }
    }

    public void getActualTimeAndDay() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HHmm");

        localTime = Integer.parseInt(date.format(currentLocalTime));
        //Check which day it is
        day = cal.get(Calendar.DAY_OF_WEEK);

        //  Log.e("RestViewH", "localtime " +localTime + day);
    }
}

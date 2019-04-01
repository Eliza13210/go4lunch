package com.oc.liza.go4lunch.util;

import android.content.Context;
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
import java.util.Locale;

public class OpeningHoursManager {

    private Context context;
    int localTime;
    int day;
    private RestaurantDetails details;
    private List<OpeningHours> openingTime = new ArrayList<>();
    private List<OpeningHours> closeTime = new ArrayList<>();

    private TextView opening_hours;

    public OpeningHoursManager(RestaurantDetails details, TextView opening_hours, Context context) {
        this.details = details;
        this.opening_hours = opening_hours;
        this.context = context;
    }

    OpeningHoursManager() {
    }

    public void checkOpening() {
        getActualTimeAndDay();
        getOpeningHoursToday();
    }

    //First check which day it is
    void getActualTimeAndDay() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HHmm", Locale.FRANCE);

        localTime = Integer.parseInt(date.format(currentLocalTime));
        //Check which day it is
        day = cal.get(Calendar.DAY_OF_WEEK);
    }

    private void getOpeningHoursToday() {
        //Get list of opening hours for the restaurant
        List<OpeningHours> openHours = details.getOpening_hours().getPeriods();
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
            try {
                if (openHours.get(i).getOpen().getDay() == dayOfWeek) {

                    //Opening and closing lunch
                    String openMorning = openHours.get(i).getOpen().getTime();
                    String closingLunch = openHours.get(i).getClose().getTime();
                    int closingLunchInt = Integer.parseInt(closingLunch);
                    int openMorningInt = Integer.parseInt(openMorning);

                    //If restaurant is open
                    //If restaurant is closing in 30 minutes
                    if ((localTime - closingLunchInt) < 30 && (localTime - closingLunchInt) > 0) {
                        opening_hours.setText(R.string.closing_soon);
                    }
                    //If restaurant is closed
                    else if (localTime > closingLunchInt) {
                        opening_hours.setText(R.string.closed);
                    }
                    //If restaurant is not yet open for lunch
                    else if (localTime < openMorningInt) {
                        String str = Integer.toString(openMorningInt);
                        str = new StringBuilder(str).insert(str.length() - 2, ".").toString();
                        String text = context.getString(R.string.opens_at) + str + "pm";
                        opening_hours.setText(text);
                    } else if (localTime < closingLunchInt) {
                        String str = Integer.toString(closingLunchInt);

                        str = new StringBuilder(str).insert(str.length() - 2, ".").toString();
                        String text = context.getString(R.string.open_until) + str + "pm";
                        opening_hours.setText(text);

                    } else if (details.getOpening_hours().isOpen_now()) {
                        opening_hours.setText(R.string.open);

                        Log.e("open", " nothing matched " + localTime + " " + closingLunchInt + openMorningInt);
                    } else if (!details.getOpening_hours().isOpen_now()) {
                        opening_hours.setText(R.string.closed);
                        Log.e("open", " nothing matched " + localTime + " " + closingLunchInt + openMorningInt);

                    }
                }
            } catch (Exception e) {
                if (details.getOpening_hours().isOpen_now()) {
                    opening_hours.setText(context.getString(R.string.open));

                } else if (!details.getOpening_hours().isOpen_now()) {
                    opening_hours.setText(context.getString(R.string.closed));
                }
            }
        }
    }
}

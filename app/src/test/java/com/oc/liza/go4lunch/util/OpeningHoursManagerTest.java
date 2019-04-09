package com.oc.liza.go4lunch.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.oc.liza.go4lunch.controllers.RestaurantActivity;
import com.oc.liza.go4lunch.models.OpeningHours;
import com.oc.liza.go4lunch.models.RestaurantDetails;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public class OpeningHoursManagerTest {

    private RestaurantDetails details = new RestaurantDetails();
    Context context = Mockito.mock(Context.class);
    TextView textView = Mockito.mock(TextView.class);

    private OpeningHoursManager manager = new OpeningHoursManager(details, textView, context);
    private RestaurantDetails opening_hours = new RestaurantDetails();
    //Get periods
    private List<OpeningHours> listOpenHours = new ArrayList<>();
    //Get open and close
    private OpeningHours openingHours = new OpeningHours();
    private OpeningHours open = new OpeningHours();
    private OpeningHours close = new OpeningHours();

    @Before
    public void setUp() {
        manager.getActualTimeAndDay();
        manager.day = Calendar.SUNDAY;
    }

    @Test
    public void getOpeningHoursToday_whenClosingInThirtyMinutes_thenReturnClosingSoon() {

        open.setDay(0);
        //Set time
        String openTime = String.valueOf(manager.localTime - 30);
        open.setTime(openTime);
        String closeTime = String.valueOf(manager.localTime + 15);
        close.setTime(closeTime);
        //And add
        openingHours.setOpen(open);
        openingHours.setClose(close);
        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);
        manager.getOpeningHoursToday();

        assertEquals(1, details.getOpening_hours().getPeriods().size());
        assertEquals("Closing soon", manager.text);
    }


    @Test
    public void getOpeningHoursToday_whenClosed_thenReturnClosed() {
        //Set time
        String openTime = String.valueOf(manager.localTime - 120);
        open.setTime(openTime);
        String closeTime = String.valueOf(manager.localTime - 60);
        close.setTime(closeTime);
        //And add
        openingHours.setOpen(open);
        openingHours.setClose(close);
        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);
        manager.getOpeningHoursToday();

        assertEquals("Closed", manager.text);
    }

    @Test
    public void getOpeningHoursToday_whenNotOpen_thenNotYetOpen() {

        //Set time
        String openTime = String.valueOf(manager.localTime + 60);
        open.setTime(openTime);
        String closeTime = String.valueOf(manager.localTime + 120);
        close.setTime(closeTime);
        //And add
        openingHours.setOpen(open);
        openingHours.setClose(close);
        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);
        manager.getOpeningHoursToday();

        assertEquals("Not yet open", manager.text);
    }

    @Test
    public void getOpeningHoursToday_whenOpen_thenReturnClosingAt() {

        //Set time
        String openTime = String.valueOf(manager.localTime - 60);
        open.setTime(openTime);
        String closeTime = String.valueOf(manager.localTime + 60);
        close.setTime(closeTime);
        //And add
        openingHours.setOpen(open);
        openingHours.setClose(close);
        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);
        manager.getOpeningHoursToday();

        String str = Integer.toString(Integer.valueOf(closeTime));
        str = new StringBuilder(str).insert(str.length() - 2, ".").toString();

        assertEquals("null" + str + "pm", manager.text);
    }


    @Test
    public void getOpeningHoursToday_whenIsOpenNow_thenReturnIsOpenNow() {

        //Set is open
        opening_hours.setOpen_now(true);
        //And add
        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);
        manager.getOpeningHoursToday();

        assertEquals("Is open now", manager.text);
    }

    @Test
    public void getOpeningHoursToday_whenIsClosed_thenReturnIsNotOpenNow() {

        //Set is open
        opening_hours.setOpen_now(false);
        //And add
        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);
        manager.getOpeningHoursToday();

        assertEquals("Is not open now", manager.text);
    }
}
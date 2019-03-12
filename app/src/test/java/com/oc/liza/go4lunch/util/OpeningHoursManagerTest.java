package com.oc.liza.go4lunch.util;

import android.widget.TextView;

import com.oc.liza.go4lunch.models.OpeningHours;
import com.oc.liza.go4lunch.models.RestaurantDetails;
import com.oc.liza.go4lunch.models.firebase.Restaurant;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OpeningHoursManagerTest {

    @Test
    public void getOpeningHoursToday() {
        RestaurantDetails details=new RestaurantDetails();
        RestaurantDetails opening_hours=new RestaurantDetails();

        List<OpeningHours> listOpenHours=new ArrayList<>();

        OpeningHours openingHours=new OpeningHours();
        OpeningHours open=new OpeningHours();
        openingHours.setOpen(open);

        listOpenHours.add(openingHours);
        opening_hours.setPeriods(listOpenHours);
        details.setOpening_hours(opening_hours);

        assertEquals(1, details.getOpening_hours().getPeriods().size());
    }

    @Test
    public void getActualTimeAndDay() {
        OpeningHoursManager manager = new OpeningHoursManager();
        manager.getActualTimeAndDay();
        assertNotNull(manager.day);
        assertNotNull(manager.localTime);
    }
}
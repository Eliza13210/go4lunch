package com.oc.liza.go4lunch.network;

import com.oc.liza.go4lunch.models.Restaurants;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RestaurantStreamTest {
    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>() {
            @Override
            public Scheduler apply(Callable<Scheduler> schedulerCallable) {
                return Schedulers.trampoline();
            }
        });
    }

    @AfterClass
    public static void tearDownClass() {
        RxAndroidPlugins.reset();
    }


    @Test
    public void getRestaurantsStream_shouldReturnObject() {
        //1 - Get the stream
        Observable<Restaurants> observable = RestaurantStream.fetchNearbyRestaurantsStream
                ("-33.8670522,151.1957362");
        //2 - Create a new TestObserver
        TestObserver<Restaurants> testObserver = new TestObserver<>();
        //3 - Launch observable
        observable.subscribeWith(testObserver)
                .assertNoErrors() // 3.1 - Check if no errors
                .assertNoTimeout() // 3.2 - Check if no Timeout
                .awaitTerminalEvent(); // 3.3 - Await the stream terminated before continue

        // 4 - Get list of user fetched
        Restaurants restaurants = testObserver.values().get(0);
        List<Restaurants> results = new ArrayList<>();

        assertNotNull(results);
    }

}
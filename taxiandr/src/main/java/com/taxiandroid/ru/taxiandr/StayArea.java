package com.taxiandroid.ru.taxiandr;

import java.util.ArrayList;

/**
 * Created by saperov on 28.11.15.
 */
public class StayArea {
    public String stay;
    static ArrayList<StayArea> stayAreas;

    public StayArea(String stay){
        this.stay=stay;
    }

    public static ArrayList<StayArea> createAreas(){
        stayAreas = new ArrayList<StayArea>();
        int cnt;
        cnt = MainActivity.stay.size();
        for (int i=0; i<cnt; i++) {
            stayAreas.add(new StayArea(MainActivity.stay.get(i)));
        }

        return stayAreas;
    }
}

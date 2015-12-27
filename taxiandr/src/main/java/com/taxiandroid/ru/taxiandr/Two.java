package com.taxiandroid.ru.taxiandr;

import java.util.ArrayList;

/**
 * Created by saperov on 01.11.15.
 */
public class Two {
    public String two_item;

    static ArrayList<Two> two_items;

    public Two(String two_item) {
        this.two_item = two_item;

    }
    public static ArrayList<Two> getTwoItem() {
        // ArrayList<User> users = new ArrayList<User>();
        two_items = new ArrayList<Two>();
        //two_items.add(new Two("Назначение поездки"));
        two_items.add(new Two("СМС о прибытии"));
        two_items.add(new Two("Звонок абоненту"));
        two_items.add(new Two("Таксометр"));
        two_items.add(new Two("Отказ"));
        two_items.add(new Two("Маршрут"));
        return two_items;
    }
}

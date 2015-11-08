package com.taxiandroid.ru.taxiandr;

import java.util.ArrayList;

/**
 * Created by saperov on 28.10.15.
 */
public class Orders {
    public String adres;
    public String sroch;
    public String indiv;
    static ArrayList<Orders> orders;

    public Orders(String adres, String sroch, String indiv) {
        this.adres = adres;
        this.sroch = sroch;
        this.indiv = indiv;
    }

    public static ArrayList<Orders> getOrders() {
        // ArrayList<User> users = new ArrayList<User>();
        orders = new ArrayList<Orders>();
        /*orders.add(new Orders("Парковый Гагарина 5а/1", "срочный", "общий"));
        orders.add(new Orders("Алексеевские планы Ореховая 15 возле шлагбаума", "срочный", "общий"));*/
        orders.add(new Orders("Нет заказов", "срочный", "индивидуальный"));
        return orders;
    }

    public  static ArrayList<Orders> UpdateOrders() {
        orders = new ArrayList<Orders>();
        int cnt;
        cnt = MainActivity.adres.size();
        for (int i=0; i<cnt; i++) {
            orders.add(new Orders(MainActivity.adres.get(i), "срочный", "общий"));
        }
        return  orders;
    }
}

package com.taxiandroid.ru.taxiandr;

/**
 * Created by saperov on 02.11.15.
 */
public class MyVariables {
    public static String SAVED_TEXT_1="";
    public static String SAVED_TEXT_2="";
    public static String SAVED_TEXT_3="";
    public static boolean BRIGTNESSHIGH=true;
    public static String SAVED_TEXT_4=""; //адрес заказа

    //public static String HTTPAdress = "http://pchelka.teleknot.ru/api/"; //Адрес отправки НТТР запросов "http://pchelka.teleknot.ru/api/" или "http://192.168.28.19/api/"

    public static String HTTPAdress = "http://192.168.28.19:3000/api/"; //Адрес отправки НТТР запросов

    public static float cost_km_city = 0; //стоимость руб/км город
    public static float cost_km_suburb =0; //стоимость руб/км пригород
    public static float cost_km_intercity=0; //стоимость руб/км район
    public static float cost_km_n1=0; //стоимость руб/км межгород
    public static float cost_stopping=0; //стоимость стоянки руб по требованию пассажира
    public static float cost_passenger_boarding_day=0; //стоимость посадки руб днем
    public static float cost_passenger_boarding_night=0; //стоимость посадки руб ночью
    public static float cost_passenger_pre_boarding_day=0; //стоимость предварительного вызова днем
    public static float cost_passenger_pre_boarding_night=0; //стоимость предварительного вызова ночью
    public static String Lat=""; //широта
    public static String Lon=""; //долгота
    public static boolean InOuExcept = false;
    public static float Itogo=0;

}

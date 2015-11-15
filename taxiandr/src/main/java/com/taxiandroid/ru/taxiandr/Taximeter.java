package com.taxiandroid.ru.taxiandr;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by saperov on 13.11.15.
 */
public class Taximeter {
    private Location mlocation;
    static String Lat;
    static String Lon;
    static long OldTime=0, NewTime=0, TimeInterval, TimeItogo=0;
    static float OldSpeed, NewSpeed, AverageSpeed, Distance, Cost, ItogKmGorod, ItogKmPrig, ItogKmRn, ItogKmMg=0;
    public static float Itogo=0;
    static boolean tutGorod=false; //в городе
    static boolean tutVG=false;    //на военном городке
    static boolean tutPG=false;    //в пригороде
    static boolean tutRn=false;    //в районе
    static boolean tutRn2=false;    //тоже в районе
   // double latCurrent;
    //double lonCurrent;
    //double lonEtalon;
    //boolean PointG1;
    static double d;
    static double da;
    static double db;
    static double ta;
    static double tb;
    static double XIntersect;
    static double YIntersect;

    static boolean StartTax=false;
    static boolean FirstTime=true;
    static boolean PauseTax=true;

    // Пустой констуктор
    public Taximeter() {

    }
    // Конструктор с параметрами
    public Taximeter(Location location) {
        this.mlocation = location;
    }

    public Location getMlocation (){
        return this.mlocation;
    }

    public void setMlocation(Location location){
        this.mlocation = location;
    }

    public static String showLocation(Location location) {
     //  this.mlocation = location;

        if (location == null)
            return null;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Lat=formatLat(location);
            Lat = Lat.replace(",", ".");
            MyVariables.Lat=Lat;
            Lon=formatLon(location);
            Lon=Lon.replace(',', '.');
            MyVariables.Lon=Lon;
            //Fragment6.TaxometrMain[12]="Сработала showLocation";
            if (StartTax) {
                showTaxometr(location);
            }
            // etResponse.setText("Your coordinates:" + " Lat=" + Lat + " ,Lon=" + Lon + PswCorrect);
            // new HttpAsyncTaskPost().execute(MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/setcoord");
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Lat=formatLat(location);
            Lat = Lat.replace(",", ".");
            MyVariables.Lat=Lat;
            Lon=formatLon(location);
            Lon=Lon.replace(',', '.');
            MyVariables.Lon=Lon;
            // etResponse.setText("Your coordinates:" + " Lat=" + Lat + " ,Lon=" + Lon + PswCorrect);
            // new HttpAsyncTaskPost().execute(MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/setcoord");
        }
        return "Lat="+Lat;
    }
    @SuppressLint("DefaultLocale") static private String formatLat(Location location) {
        if (location == null)
            return "";
        return String.format("%1$.10f",location.getLatitude());
    }

    @SuppressLint("DefaultLocale") static private String formatLon(Location location) {
        if (location == null)
            return "";
        return String.format("%1$.10f",location.getLongitude());
    }

    @SuppressLint("DefaultLocale") static private void showTaxometr(Location location) {
        // if (Fragment6.StartTax) {
        if (FirstTime) {
            OldTime=NewTime;
            NewTime= SystemClock.uptimeMillis();
            TimeInterval=0;
            TimeItogo = 0;
            NewSpeed = 0;
        }
        else {
            OldTime=NewTime;
            NewTime=SystemClock.uptimeMillis();
            TimeInterval=NewTime-OldTime;
            TimeItogo = TimeItogo + TimeInterval;
        }
        OldSpeed=NewSpeed;
        NewSpeed=location.getSpeed();
        AverageSpeed=(NewSpeed+OldSpeed)/2;
        Distance=AverageSpeed*TimeInterval/1000; //  м/с * сек

      		/*Определяем в каком месте находимся*/
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.873696, 40.084303, 45.884451, 40.130909, 45.845751, 40.168203, 45.839697, 40.130051)) tutGorod=true;
        else tutGorod=false;
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.869187, 40.083370, 45.869277, 40.091824, 45.862434, 40.100364, 45.862135, 40.082426)) tutVG=true;
        else tutVG=false;
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.854390, 40.060062, 45.890246, 40.130443, 45.836334, 40.180654, 45.810375, 40.144519)) tutPG=true;
        else tutPG=false;
        tutGorod=tutGorod|tutVG; //хоть город хоть военный считаем что город
        //дальше будем искать где мы - в районе или в межгороде
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 46.1089, 40.0307, 45.9595, 40.5001, 45.6071, 40.5629, 45.5917, 40.0397)) tutRn=true; //тут район, большой квадрат
        else tutRn=false;
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.8831, 39.8162, 45.9905, 40.0537, 45.8125, 40.1073, 45.8226, 39.8454)) tutRn2=true; //тут район, малый квадрат Новорождественская
        else tutRn2=false;
        tutRn=tutRn|tutRn2; //хоть большой квадрат хоть малый считаем что это район

      		/* В зависимости от места выбираем тариф */
        if (PauseTax==false ) { //если кнопка Старт не зеленая сумма не изменяется
            if (AverageSpeed!=0) { //если скорость не равна нулю
                if (tutRn==false) { //если это межгород
                    //это межгород
                    ItogKmMg=ItogKmMg+Distance;
                    Cost = Distance*MyVariables.cost_km_n1/1000;
                }
                else { //если это не межгород
                    if ((tutPG==false) & (tutGorod==false)) {  //проверяем может это район
                        //значит это район
                        ItogKmRn=ItogKmRn+Distance;
                        Cost = Distance*MyVariables.cost_km_intercity/1000;
                    }
                    if ((tutPG==true) & (tutGorod==false)) { //если это пригород
                        //значит это пригород
                        ItogKmPrig=ItogKmPrig+Distance;
                        Cost = Distance*MyVariables.cost_km_suburb/1000;
                    }
                    if ((tutPG==true) & (tutGorod==true)) { //если это город
                        //значит это город
                        ItogKmGorod=ItogKmGorod+Distance;
                        Cost = Distance*MyVariables.cost_km_city/1000;
                    }
                }
            }
            else Cost = (MyVariables.cost_stopping/60) * TimeInterval/1000; //если красный светофор- новая стоимость равна 5 руб/мин 5/60 получ сек умнож на TimeInterval/1000 сек
            Itogo=Itogo+Cost;
            MyVariables.Itogo = Itogo;
        }

        //отображаем результат вычислений
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(TimeItogo),
                TimeUnit.MILLISECONDS.toMinutes(TimeItogo) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(TimeItogo) % TimeUnit.MINUTES.toSeconds(1));
      		        //String strItogo = String.format("%.2f",Itogo);
     /*   Fragment2.TaxometrMain[0]="Сумма: " + String.format("%.2f",Itogo) + " руб";
        Fragment2.TaxometrMain[1]="Время в пути: "+hms;
        Fragment2.TaxometrMain[2]="Дистанция город: " + String.format("%.1f",ItogKmGorod) +" м"; //String.format("%1$.1f",
        Fragment2.TaxometrMain[3]="Дистанция пригород: " + String.format("%.1f",ItogKmPrig) +" м";
        Fragment2.TaxometrMain[4]="Дистанция район: "+String.format("%.1f",ItogKmRn)+" м";
        Fragment2.TaxometrMain[5]="Дистанция межгород: "+String.format("%.1f",ItogKmMg)+" м";
        Fragment2.TaxometrMain[6]="Тариф "+String.valueOf(MyVariables.cost_km_city)+" руб/км город";
        Fragment2.TaxometrMain[7]="Тариф "+String.valueOf(MyVariables.cost_km_suburb)+" руб/км пригород";
        Fragment2.TaxometrMain[8]="Тариф "+String.valueOf(MyVariables.cost_km_intercity)+" руб/км район";
        Fragment2.TaxometrMain[9]="Тариф "+String.valueOf(MyVariables.cost_km_n1)+" руб/км межгород";
        Fragment2.TaxometrMain[10]="Тариф "+String.valueOf(MyVariables.cost_stopping)+" руб/мин стоянка";
        Fragment2.TaxometrMain[11]="Тариф "+String.valueOf(MyVariables.cost_passenger_boarding_day)+" руб посадка день";
        Fragment2.TaxometrMain[12]="Тариф "+String.valueOf(MyVariables.cost_passenger_boarding_night)+" руб посадка ночь";
        Fragment2.TaxometrMain[13]="Тариф "+String.valueOf(MyVariables.cost_passenger_pre_boarding_day)+" руб предвар день";
        Fragment2.adapter.notifyDataSetChanged();*/
        /*ActivityThree.buttonStart.setText(String.format("%.2f",Itogo) + " руб");
        ActivityThree.tvDistance.setText("Дистанция город: " + String.format("%.1f",ItogKmGorod) +" м");
        ActivityThree.tvStay.setText("Время в пути: "+hms);*/


        FirstTime=false;

    }

    public static boolean intersect(double XLon, double YLat, double y1, double x1, double y2, double x2, double y3, double x3, double y4, double x4) {
        //Определяется находится ли точка внутри четырехугольника, в городе или нет

        boolean E214=false;
        boolean E243=false;
        boolean E143=false;
        boolean E123=false;
        boolean E423=false;
        boolean E412=false;
        boolean E312=false;
        boolean E314=false;

        //Пересечение отрезка E-2 с отрезком 1-4
        d=(XLon-x2)*(y4-y1)-(YLat-y2)*(x4-x1);
        da=(XLon-x1)*(y4-y1)-(YLat-y1)*(x4-x1);
        db=(XLon-x2)*(YLat-y1)-(YLat-y2)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x2-XLon);
                YIntersect=YLat+ta*(y2-YLat);
                E214=false;
            }
            else E214=true;
        }
        //Пересечение отрезка E-2 с отрезком 4-3
        d=(XLon-x2)*(y4-y3)-(YLat-y2)*(x4-x3);
        da=(XLon-x3)*(y4-y3)-(YLat-y3)*(x4-x3);
        db=(XLon-x2)*(YLat-y3)-(YLat-y2)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x2-XLon);
                YIntersect=YLat+ta*(y2-YLat);
                E243=false;
            }
            else E243=true;
        }
        //Пересечение отрезка E-1 с отрезком 4-3
        d=(XLon-x1)*(y4-y3)-(YLat-y1)*(x4-x3);
        da=(XLon-x3)*(y4-y3)-(YLat-y3)*(x4-x3);
        db=(XLon-x1)*(YLat-y3)-(YLat-y1)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x1-XLon);
                YIntersect=YLat+ta*(y1-YLat);
                E143=false;
            }
            else E143=true;
        }
        //Пересечение отрезка E-1 с отрезком 2-3
        d=(XLon-x1)*(y2-y3)-(YLat-y1)*(x2-x3);
        da=(XLon-x3)*(y2-y3)-(YLat-y3)*(x2-x3);
        db=(XLon-x1)*(YLat-y3)-(YLat-y1)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x1-XLon);
                YIntersect=YLat+ta*(y1-YLat);
                E123=false;
            }
            else E123=true;
        }
        //Пересечение отрезка E-4 с отрезком 2-3
        d=(XLon-x4)*(y2-y3)-(YLat-y4)*(x2-x3);
        da=(XLon-x3)*(y2-y3)-(YLat-y3)*(x2-x3);
        db=(XLon-x4)*(YLat-y3)-(YLat-y4)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x4-XLon);
                YIntersect=YLat+ta*(y4-YLat);
                E423=false;
            }
            else E423=true;
        }
        //Пересечение отрезка E-4 с отрезком 1-2////////
        d=(XLon-x4)*(y2-y1)-(YLat-y4)*(x2-x1);
        da=(XLon-x1)*(y2-y1)-(YLat-y1)*(x2-x1);
        db=(XLon-x4)*(YLat-y1)-(YLat-y4)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x4-XLon);
                YIntersect=YLat+ta*(y4-YLat);
                E412=false;
            }
            else E412=true;
        }
        //Пересечение отрезка E-3 с отрезком 1-4
        d=(XLon-x3)*(y4-y1)-(YLat-y3)*(x4-x1);
        da=(XLon-x1)*(y4-y1)-(YLat-y1)*(x4-x1);
        db=(XLon-x3)*(YLat-y1)-(YLat-y3)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x3-XLon);
                YIntersect=YLat+ta*(y3-YLat);
                E314=false;
            }
            else E314=true;
        }
        //Пересечение отрезка E-3 с отрезком 1-2
        d=(XLon-x3)*(y2-y1)-(YLat-y3)*(x2-x1);
        da=(XLon-x1)*(y2-y1)-(YLat-y1)*(x2-x1);
        db=(XLon-x3)*(YLat-y1)-(YLat-y3)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x3-XLon);
                YIntersect=YLat+ta*(y3-YLat);
                E312=false;
            }
            else E312=true;
        }
        if ((E214&E243)&(E143&E123)&(E423&E412)&(E312&E314)) return true;
        else return false;

        //закончили определение четырехугольника мы в городе


    }


}

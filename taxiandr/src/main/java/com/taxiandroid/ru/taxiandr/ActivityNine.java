package com.taxiandroid.ru.taxiandr;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityNine extends AppCompatActivity {
    MapView map;
    ArrayList<GeoPoint> wp2 = new ArrayList<GeoPoint>();
    RoadManager roadManager = new OSRMRoadManager();
    GeoPoint startPoint, curPoint, oldPoint, newPoint;
    String[] Lat;
    String[] Lon;
    Marker myMarker;
    IMapController mapController;
    private static final String TAG = "myLogs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        startPoint = new GeoPoint(45.86178811, 40.11608362);
        mapController = map.getController();
        mapController.setZoom(14);
        mapController.setCenter(startPoint);

        myMarker = new Marker(map);
        myMarker.setPosition(startPoint);
        Drawable nodeIcon = getResources().getDrawable(R.drawable.moreinfo_arrow);
        myMarker.setIcon(nodeIcon);
        myMarker.setRotation(-90);
        map.getOverlays().add(myMarker);

        //получаем полилайн из пришедшей строки
        wp2 = PolylineEncoder.decode(MyVariables.RouteGeometry, 1, false);
        Road road = roadManager.getRoad(wp2);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, this);
        map.getOverlays().add(roadOverlay);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // CheckOrder();
                new MapAsyncTask().execute();
            }
        }, 0, 5000);

        Lat = new String[2];
        Lon = new String[2];
        for (int i=0; i<2; i++) {
            Lon[i] = "0";
            Lat[i] = "0";
        }
    }

    public class MapAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            if (!Lon[0].equals(MyVariables.Lon)|!Lat[0].equals(MyVariables.Lat)) {
                Lon[1] = Lon[0];
                Lat[1] = Lat[0];
                Lon[0] = MyVariables.Lon;
                Lat[0] = MyVariables.Lat;
                Log.d(TAG, "Едем");
            } else {
                Log.d(TAG, "Стоим");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            curPoint = new GeoPoint(Double.parseDouble(Lat[0]), Double.parseDouble(Lon[0]));
            oldPoint = new GeoPoint(Double.parseDouble(Lat[1]), Double.parseDouble(Lon[1]));
       // curPoint = new GeoPoint(GeoPoint.fromIntString(Lat[0] + "," + Lon[0]));
            mapController.setCenter(curPoint);
            map.getOverlays().remove(myMarker);
            myMarker = new Marker(map);
            myMarker.setPosition(curPoint);
            Drawable nodeIcon = getResources().getDrawable(R.drawable.moreinfo_arrow);
            myMarker.setIcon(nodeIcon);
            myMarker.setRotation(-90);
            map.getOverlays().add(myMarker);
           // map.setMapOrientation(90);


            float bearing = (float) oldPoint.bearingTo(curPoint);
            map.setMapOrientation(360-bearing);
            Log.d(TAG, "bearing" + String.valueOf(bearing));
        }
    }

}

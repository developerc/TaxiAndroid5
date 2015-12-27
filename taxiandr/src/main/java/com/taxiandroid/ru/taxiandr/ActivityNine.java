package com.taxiandroid.ru.taxiandr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class ActivityNine extends AppCompatActivity {
    MapView map;
    ArrayList<GeoPoint> wp2 = new ArrayList<GeoPoint>();
    RoadManager roadManager = new OSRMRoadManager();
    GeoPoint startPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        startPoint = new GeoPoint(45.86178811, 40.11608362);
        IMapController mapController = map.getController();
        mapController.setZoom(14);
        mapController.setCenter(startPoint);

        //получаем полилайн из пришедшей строки
        wp2 = PolylineEncoder.decode(MyVariables.RouteGeometry, 1, false);
        Road road = roadManager.getRoad(wp2);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, this);
        map.getOverlays().add(roadOverlay);

    }

}

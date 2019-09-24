/* Copyright 2017 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.esri.arcgisruntime.displaymapoffline;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.MobileMapPackage;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    public static final String OFFLINE_MAP_DATA = "us-navigation.mmpk";

    private final SpatialReference wgs84 = SpatialReference.create(4236);
    private MapView mMapView;

    private void setupMap() {
        if (mMapView != null) {
            Basemap.Type basemapType = Basemap.Type.STREETS_VECTOR;
            double latitude = 34.09042;
            double longitude = -118.71511;
            int levelOfDetail = 11;
            ArcGISMap map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mMapView.setMap(map);
        }
    }

    private void setupOfflineMap() {
        if (mMapView != null) {
            File path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            //File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
            //File path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);

            //File path = new File("/storage/sdcard0/Download/");
            File mmpkFile = new File(path, OFFLINE_MAP_DATA);
            if (mmpkFile.exists()) {
                final MobileMapPackage mapPackage = new MobileMapPackage(mmpkFile.getAbsolutePath());
                mapPackage.addDoneLoadingListener(() -> {

                    // check load status and that the mobile map package has at least one map
                    if (mapPackage.getLoadStatus() == LoadStatus.LOADED && !mapPackage.getMaps().isEmpty()) {
                        mMapView.setMap(mapPackage.getMaps().get(0));

                        // create a map with the BasemapType topographic
                       // ArcGISMap map = new ArcGISMap(Basemap.Type.OCEANS, 56.075844, -2.681572, 11);
                        // set the map to be displayed in this view
                       // mMapView.setMap(map);
                        // add graphics overlay to MapView.
                        GraphicsOverlay graphicsOverlay = addGraphicsOverlay(mMapView);
                        //add some buoy positions to the graphics overlay
                        addBuoyPoints(graphicsOverlay);
                        //add boat trip polyline to graphics overlay
                    /*    addBoatTrip(graphicsOverlay);
                        //add nesting ground polygon to graphics overlay
                        addNestingGround(graphicsOverlay);
                        //add text symbols and points to graphics overlay
                        addText(graphicsOverlay);*/
                    } else {

                        // Error if the mobile map package fails to load or there are no maps included in the package,
                        // then fallback to the online map
                        Log.e("setupOfflineMap", "Cannot load " + mmpkFile.toString());
                        setupMap();
                    }
                });
                mapPackage.loadAsync();
            } else {
                Log.e("Map", mmpkFile.getAbsolutePath());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
    //    fromExtToInternal();
        setupOfflineMap();

    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }

    private GraphicsOverlay addGraphicsOverlay(MapView mapView) {
        //create the graphics overlay
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        //add the overlay to the map view
        mapView.getGraphicsOverlays().add(graphicsOverlay);
        return graphicsOverlay;
    }

    private void addBuoyPoints(GraphicsOverlay graphicOverlay) {
        //define the buoy locations
        Point buoy1Loc = new Point(-118.71513, 34.09042, wgs84);
        //Point buoy2Loc = new Point(-118.71513, 34.09043, wgs84);
        //Point buoy3Loc = new Point(-118.71512, 34.09042, wgs84);
        //Point buoy4Loc = new Point(-118.71513, 34.09042, wgs84);
        //create a marker symbol
        SimpleMarkerSymbol buoyMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10);
        //create graphics
        Graphic buoyGraphic1 = new Graphic(buoy1Loc, buoyMarker);
      //  Graphic buoyGraphic2 = new Graphic(buoy2Loc, buoyMarker);
      //  Graphic buoyGraphic3 = new Graphic(buoy3Loc, buoyMarker);
    //    Graphic buoyGraphic4 = new Graphic(buoy4Loc, buoyMarker);
        //add the graphics to the graphics overlay
        graphicOverlay.getGraphics().add(buoyGraphic1);
    //    graphicOverlay.getGraphics().add(buoyGraphic2);
     //   graphicOverlay.getGraphics().add(buoyGraphic3);
     //   graphicOverlay.getGraphics().add(buoyGraphic4);
    }

    private void addText(GraphicsOverlay graphicOverlay) {
        //create a point geometry
        Point bassLocation = new Point(-2.640631, 56.078083, wgs84);
        Point craigleithLocation = new Point(-2.720324, 56.073569, wgs84);

        //create text symbols
        TextSymbol bassRockSymbol =
                new TextSymbol(10, "Bass Rock", Color.rgb(0, 0, 230),
                        TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
        TextSymbol craigleithSymbol = new TextSymbol(10, "Craigleith", Color.rgb(0, 0, 230),
                TextSymbol.HorizontalAlignment.RIGHT, TextSymbol.VerticalAlignment.TOP);

        //define a graphic from the geometry and symbol
        Graphic bassRockGraphic = new Graphic(bassLocation, bassRockSymbol);
        Graphic craigleithGraphic = new Graphic(craigleithLocation, craigleithSymbol);
        //add the text to the graphics overlay
        graphicOverlay.getGraphics().add(bassRockGraphic);
        graphicOverlay.getGraphics().add(craigleithGraphic);
    }

    private void addBoatTrip(GraphicsOverlay graphicOverlay) {
        //define a polyline for the boat trip
        Polyline boatRoute = getBoatTripGeometry();
        //define a line symbol
        SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.rgb(128, 0, 128), 4);
        //create the graphic
        Graphic boatTripGraphic = new Graphic(boatRoute, lineSymbol);
        //add to the graphic overlay
        graphicOverlay.getGraphics().add(boatTripGraphic);
    }

    private void addNestingGround(GraphicsOverlay graphicOverlay) {
        //define the polygon for the nesting ground
        Polygon nestingGround = getNestingGroundGeometry();
        //define the fill symbol and outline
        SimpleLineSymbol outlineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, Color.rgb(0, 0, 128), 1);
        SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.DIAGONAL_CROSS, Color.rgb(0, 80, 0),
                outlineSymbol);
        //define graphic
        Graphic nestingGraphic = new Graphic(nestingGround, fillSymbol);
        //add to graphics overlay
        graphicOverlay.getGraphics().add(nestingGraphic);
    }

    private Polyline getBoatTripGeometry() {
        //a new point collection to make up the polyline
        PointCollection boatPositions = new PointCollection(wgs84);
        //add positions to the point collection
        boatPositions.add(new Point(-2.7184791227926772, 56.06147084563517));
        boatPositions.add(new Point(-2.7196807500463924, 56.06147084563517));
        boatPositions.add(new Point(-2.722084004553823, 56.062141712059706));
        boatPositions.add(new Point(-2.726375530459948, 56.06386674355254));
        boatPositions.add(new Point(-2.726890513568683, 56.0660708381432));

        //create the polyline from the point collection
        return new Polyline(boatPositions);
    }

    private Polygon getNestingGroundGeometry() {

        //a new point collection to make up the polygon
        PointCollection points = new PointCollection(wgs84);

        //add points to the point collection
        points.add(new Point(-2.643077012566659, 56.077125346044475));
        points.add(new Point(-2.6428195210159444, 56.07717324600376));
        points.add(new Point(-2.6425405718360033, 56.07774804087097));

        //create a polygon from the point collection
        return new Polygon(points);
    }
}

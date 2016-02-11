package com.innopolis.maps.innomaps;

/**
 * Created by Nikolay on 02.02.2016.
 */

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    MapView mapView; //an element of the layout
    private GoogleMap map;
    private UiSettings mSettings;

    private Marker markerFrom;
    private Marker markerTo;

    private PathFinder pathFinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_maps, container, false);

        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                // Gets to GoogleMap from the MapView and does initialization stuff
                if (mapView != null) {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.setMyLocationEnabled(true);
                    mSettings = map.getUiSettings();
                    mSettings.setZoomControlsEnabled(true);
                    LatLng university = new LatLng(55.752321, 48.744674);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 15));
                    mSettings.setIndoorLevelPickerEnabled(true);
                    map.setIndoorEnabled(true);
                    map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            // TODO: remove all deprecated calls

                            if (markerFrom == null) {
                                markerFrom = addMarker(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()));
                            }
                            if (markerTo != null) {
                                markerTo.remove();
                            }
                            markerTo = addMarker(latLng);
                            if (pathFinder == null) {
                                pathFinder = new PathFinder(map, new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()), latLng);
                            }
                            else {
                                pathFinder.setLatLng(new LatLng(map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude()), latLng);
                            }
                            pathFinder.findPath();
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
        return v;
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private Marker addMarker(LatLng point) {
        return map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.test_custom_marker)));
    }



}


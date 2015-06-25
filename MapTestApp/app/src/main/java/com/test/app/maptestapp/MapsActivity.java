package com.test.app.maptestapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<LatLng> mLatLngs;
    //public static boolean mMapIsTouched;
    //public static boolean mMapIsMoved;
    private Handler handler = new Handler();
    private TextView mTextViewZoomIn;
    private TextView mTextViewZoomOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initLatLng();
        setUpMapIfNeeded();

    }

    private void initLatLng() {
        mLatLngs = new ArrayList<>();
        mLatLngs.add(new LatLng(23.00090, 72.60271));
        mLatLngs.add(new LatLng(23.0007374, 72.6054951));
        mLatLngs.add(new LatLng(23.009132, 72.605238));
        mLatLngs.add(new LatLng(23.0051155, 72.6056886));
        mLatLngs.add(new LatLng(23.009816, 72.599938));
        mLatLngs.add(new LatLng(23.009816, 72.599938));
        mLatLngs.add(new LatLng(23.007135, 72.602073));
        mLatLngs.add(new LatLng(23.000464, 72.605195));
        mLatLngs.add(new LatLng(23.003363, 72.605174));
        mLatLngs.add(new LatLng(23.004494, 72.605839));
        mLatLngs.add(new LatLng(23.004963, 72.607008));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #buildMarkers()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            final MySupportMapFragment mySupportMapFragment = ((MySupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map));
            mMap = mySupportMapFragment.getMap();
            mTextViewZoomIn = (TextView) findViewById(R.id.zoom_in);
            mTextViewZoomIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomIn();
                }
            });
            mTextViewZoomOut = (TextView) findViewById(R.id.zoom_out);
            mTextViewZoomOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomOut();
                }
            });
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(23.00090, 72.60271)));
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMap.getUiSettings().setZoomControlsEnabled(false);
                        mySupportMapFragment.addSupportMapListener(new MySupportMapFragment.SupportMapListener() {
                            private Runnable mRunnable;

                            @Override
                            public void onMoveEnd() {

                                if (mRunnable != null) {
                                    handler.removeCallbacks(mRunnable);
                                }
                                mRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("Pins onHandler");
                                        checkVisibleMarkers(mMap, mLatLngs);
                                    }
                                };
                                handler.postDelayed(mRunnable, 250);

                            }

                            @Override
                            public void onActionUp() {

                            }

                            @Override
                            public void onActionDown() {

                            }
                        });
                        buildMarkers();
                    }
                });


            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void buildMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < mLatLngs.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions().position(mLatLngs.get(i)).title("Marker");
            mMap.addMarker(markerOptions);
            builder.include(markerOptions.getPosition());
        }

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    public void zoomIn() {
        // Map animate listener Zoom in

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn(), new GoogleMap.CancelableCallback() {

                @Override
                public void onFinish() {
                    checkVisibleMarkers(mMap, mLatLngs);
                }

                @Override
                public void onCancel() {
                }

            });
        }
    }


    public void zoomOut() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut(), new GoogleMap.CancelableCallback() {

                @Override
                public void onFinish() {
                    checkVisibleMarkers(mMap, mLatLngs);
                }

                @Override
                public void onCancel() {
                }

            });
        }
    }


    // Method to track visible pins
    public void checkVisibleMarkers(GoogleMap map, ArrayList<LatLng> markers) {

        int pinsVisible = 0;
        int pinsInVisible = 0;

        LatLngBounds mLatLngBounds = map.getProjection().getVisibleRegion().latLngBounds;

        for (int i = 0; i < markers.size(); i++) {
            if (mLatLngBounds.contains(markers.get(i))) {
                pinsVisible++;
            } else {
                pinsInVisible++;
            }
        }
        Toast.makeText(this, "Pins Visible: " + pinsVisible + " -- Pins Invisible: " + pinsInVisible + "", Toast.LENGTH_SHORT).show();
        System.out.println("Pins Visible: " + pinsVisible +
                " -- Pins Invisible: " + pinsInVisible + "");
    }


   /* private final GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            if (!mMapIsTouched) {
                checkVisibleMarkers(mMap, mLatLngs);
            }
        }
    };*/

}

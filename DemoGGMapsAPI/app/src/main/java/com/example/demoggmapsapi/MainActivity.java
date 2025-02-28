    package com.example.demoggmapsapi;

    import android.content.Context;
    import android.content.pm.PackageManager;
    import android.location.Location;
    import android.location.LocationManager;
    import android.os.Bundle;
    import android.view.Menu;
    import android.view.MenuItem;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;

    import android.location.LocationListener;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.PolylineOptions;

    public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
        private GoogleMap myMap;
        private LocationManager locationManager;
        private Marker userMarker;
        private static final int LOCATION_PERMISSION_REQUEST = 1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            myMap = googleMap;
            LatLng hoChiMinh = new LatLng(10.7769, 106.7009);
            LatLng haNoi = new LatLng(21.0278, 105.8048);
            myMap.addMarker(new MarkerOptions().position(hoChiMinh).title("TP. Hồ Chí Minh"));
            myMap.addMarker(new  MarkerOptions().position(haNoi).title("Hà Nội"));
            myMap.addPolyline(new PolylineOptions()
                    .add(hoChiMinh)
                    .add(haNoi));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hoChiMinh, 10));
            myMap.getUiSettings().setZoomControlsEnabled(true);
            myMap.getUiSettings().setCompassEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
            myMap.getUiSettings().setZoomGesturesEnabled(true);

            // Initialize LocationManager
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Request location updates & move camera to user location
            getUserLocation();

//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, 3000, 1, new LocationListener() {
//                        @Override
//                        public void onLocationChanged(@NonNull Location location) {
//                            updateUserLocation(location);
//                        }
//
//                        @Override
//                        public void onStatusChanged(String provider, int status, Bundle extras) {} // Optional
//
//                        @Override
//                        public void onProviderEnabled(String provider) {} // Optional
//
//                        @Override
//                        public void onProviderDisabled(String provider) {} // Optional
//                    });
        }

        private void getUserLocation() {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                return;
            }

            // Get last known location
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                updateUserLocation(lastKnownLocation);
            }

            // Request real-time location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    updateUserLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            });
        }


        @Override
        public boolean onCreateOptionsMenu (@NonNull  Menu menu){
           // return super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull  MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.mapNone) {
                myMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
            if (id == R.id.mapNormal) {
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
            if (id == R.id.mapSatelite) {
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
            if (id == R.id.mapHybrid) {
                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
            if (id == R.id.mapTerrain) {
                myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
            return super.onOptionsItemSelected(item);
        }

        private void updateUserLocation(Location location) {
            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (userMarker == null) {
                userMarker = myMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Location"));
            } else {
                userMarker.setPosition(userLatLng);
            }

            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
        }
    }

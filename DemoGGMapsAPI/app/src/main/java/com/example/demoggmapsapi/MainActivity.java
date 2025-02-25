package com.example.demoggmapsapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Ứng dụng demo sử dụng Google Maps API để hiển thị bản đồ, marker, polyline và các tính năng tương tác.
 * - Hiển thị bản đồ với marker tại TP. Hồ Chí Minh và Hà Nội.
 * - Vẽ đường polyline kết nối TP. Hồ Chí Minh, Đà Nẵng và Hà Nội.
 * - Hỗ trợ hiển thị vị trí hiện tại của người dùng nếu có quyền truy cập vị trí.
 * - Cho phép thay đổi loại bản đồ (Normal, Satellite, Terrain, Hybrid) qua menu.
 * - Lắng nghe sự kiện click vào bản đồ và marker để hiển thị thông tin qua Toast.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo fragment bản đồ
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Khởi tạo bản đồ khi sẵn sàng, thêm marker, polyline và thiết lập các sự kiện.
     * @param googleMap Đối tượng GoogleMap để thao tác với bản đồ.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        // Kiểm tra và yêu cầu quyền truy cập vị trí
        checkLocationPermission();

        // Thêm marker cho Hồ Chí Minh
        LatLng hoChiMinh = new LatLng(10.7769, 106.7009);
        myMap.addMarker(new MarkerOptions()
                .position(hoChiMinh)
                .title("TP. Hồ Chí Minh")
                .snippet("Thành phố lớn nhất Việt Nam"));

        // Thêm marker với biểu tượng tùy chỉnh cho Hà Nội
        LatLng hanoi = new LatLng(21.0278, 105.8342);
        myMap.addMarker(new MarkerOptions()
                .position(hanoi)
                .title("Hà Nội")
                .snippet("Thủ đô của Việt Nam")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        // Vẽ đường nối giữa Hà Nội và TP.HCM
        drawPolyline(hoChiMinh, hanoi);

        // Di chuyển camera đến Hồ Chí Minh với mức zoom là 10
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hoChiMinh, 10));

        // Thiết lập các sự kiện bản đồ
        setupMapListeners();
    }

    /**
     * Vẽ một đường polyline nối giữa hai điểm với điểm trung gian là Đà Nẵng.
     * @param start Điểm bắt đầu (TP. Hồ Chí Minh).
     * @param end Điểm kết thúc (Hà Nội).
     */
    private void drawPolyline(LatLng start, LatLng end) {
        List<LatLng> points = new ArrayList<>();
        points.add(start);
        points.add(new LatLng(16.0678, 108.2208)); // Đà Nẵng
        points.add(end);

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(points)
                .width(5)
                .color(getResources().getColor(android.R.color.holo_red_dark));

        myMap.addPolyline(polylineOptions);
    }

    /**
     * Thiết lập các sự kiện lắng nghe click vào bản đồ và marker.
     */
    private void setupMapListeners() {
        myMap.setOnMapClickListener(latLng -> {
            Toast.makeText(this, "Tọa độ: " + latLng.latitude + ", " + latLng.longitude, Toast.LENGTH_SHORT).show();
        });

        myMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(this, "Marker: " + marker.getTitle(), Toast.LENGTH_SHORT).show();
            return false; // Trả về false để hiển thị info window
        });
    }

    /**
     * Kiểm tra quyền truy cập vị trí và yêu cầu nếu chưa có.
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    /**
     * Bật tính năng hiển thị vị trí hiện tại và nút định vị nếu có quyền.
     */
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    /**
     * Xử lý kết quả yêu cầu quyền truy cập vị trí.
     * @param requestCode Mã yêu cầu quyền.
     * @param permissions Danh sách quyền được yêu cầu.
     * @param grantResults Kết quả cấp quyền.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Cần quyền vị trí để hiển thị vị trí của bạn", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Tạo menu để chuyển đổi loại bản đồ.
     * @param menu Đối tượng Menu để thêm các mục.
     * @return true nếu menu được tạo thành công.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Bản đồ thường");
        menu.add(0, 2, 0, "Bản đồ vệ tinh");
        menu.add(0, 3, 0, "Bản đồ địa hình");
        menu.add(0, 4, 0, "Bản đồ hỗn hợp");
        return true;
    }

    /**
     * Xử lý sự kiện khi chọn mục trong menu để thay đổi loại bản đồ.
     * @param item Mục menu được chọn.
     * @return true nếu xử lý thành công, ngược lại gọi super.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case 2:
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case 3:
                myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case 4:
                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
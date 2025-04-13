package org.bihe.livebin.ui.activities;

import static org.bihe.livebin.data.prefrences.PreferencesManager.PREF_KEY_PERFORMANCE;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.bihe.livebin.R;
import org.bihe.livebin.data.AppData;
import org.bihe.livebin.data.db.DbManager;
import org.bihe.livebin.data.db.dao.CustomerDao;
import org.bihe.livebin.data.model.Customer;
import org.bihe.livebin.data.model.LocationObj;
import org.bihe.livebin.data.model.User;
import org.bihe.livebin.data.network.impl.CustomerServiceImpl;
import org.bihe.livebin.data.network.impl.LocationServiceImpl;
import org.bihe.livebin.data.network.impl.UserServiceImpl;
import org.bihe.livebin.data.prefrences.PreferencesManager;
import org.bihe.livebin.databinding.ActivityMapBinding;
import org.bihe.livebin.utils.ResultListener;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity {

    private ActivityMapBinding binding;
    private MapView map;
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 2000;
    private List<GeoPoint> points = new ArrayList<>();
    private User currentUser;
    private LocationServiceImpl locationService;
    private CustomerServiceImpl customerService;
    private UserServiceImpl userService;
    private PreferencesManager preferencesManager;

    private ExecutorService executorService;
    private CustomerDao customerDao;

    private Map<String, Integer> userColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setupMap();
        checkUser();
    }

    private void init() {
        currentUser = AppData.getInstance().getCurrentUser();
        locationService = new LocationServiceImpl(this);
        customerService = new CustomerServiceImpl(this);
        preferencesManager = PreferencesManager.getInstance(this);
        userService = new UserServiceImpl();
        userColors = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        customerDao = DbManager.getInstance(this).customerDao();
    }

    private void setupMap() {
        if (binding.map == null) {
            return;
        }
        map = binding.map;
        map.setUseDataConnection(true);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        map.getController().setZoom(15);
        GeoPoint startPoint = new GeoPoint(32.6546, 51.6679);
        map.getController().setCenter(startPoint);
    }

    private int getRandomColor() {
        return Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }


    private void checkUser() {
        String userPerformance = preferencesManager.get(PREF_KEY_PERFORMANCE, "");
        if (userPerformance.equals(getString(R.string.admin))) {
            startAdminAutoUpdate();
        } else if (userPerformance.equals(getString(R.string.performance1))) {
            startMarketerAutoUpdate();
        } else {
            startCollectionAutoUpdate();
        }
    }

    private void addCustomersMarker(GeoPoint point, String title) {
        if (map == null) {
            return;
        }
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        map.invalidate();
    }

    private void addUsersMarker(GeoPoint point, String title) {
        if (map == null) {
            return;
        }
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle(title);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_location);
        if (drawable != null) {
            marker.setIcon(drawable);
        }
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(marker);
        map.invalidate();
    }

    private void drawRoute(List<GeoPoint> points) {
        if (map == null) {
            return;
        }
        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(Color.BLUE);
        line.setWidth(8.0f);
        map.getOverlays().add(line);
        map.invalidate();
    }

    private void drawUsersRoute(List<GeoPoint> points, int color) {
        if (map == null) {
            return;
        }
        Polyline line = new Polyline();
        line.setPoints(points);
        line.setColor(color);
        line.setWidth(8.0f);
        map.getOverlays().add(line);
        map.invalidate();
    }


    private void userRoute() {
        if (map == null) {
            return;
        }
        map.getOverlays().clear();
        points.clear();
        try {
        locationService.getLocationsOfUser(currentUser.getId(), new ResultListener<List<LocationObj>>() {
            @Override
            public void onResult(List<LocationObj> locations) {
                for (LocationObj location : locations) {
                    GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                    points.add(point);
                }
                GeoPoint point = new GeoPoint(locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());
                addUsersMarker(point, currentUser.getUsername());
                drawRoute(points);
            }

            @Override
            public void onError(Throwable throwable) {
                showToastMessage(throwable.getMessage());
            }
        });
    } catch (Exception e) {
        showToastMessage("خطای غیرمنتظره: " + e.getMessage());
    }
    }

    private void markUserCustomersLocations() {
        if (map == null) {
            return;
        }
        executorService.execute(() -> {
            List<Customer> customers = customerDao.getCustomersByUserId(currentUser.getId());
            for (Customer customer : customers) {
                addCustomersMarker(new GeoPoint(customer.getLatitude(), customer.getLongitude()), customer.getCustomerName());
            }
        });

    }

    private void markCustomersLocations() {
        if (map == null) {
            return;
        }
        customerService.getAllCustomers(new ResultListener<List<Customer>>() {
            @Override
            public void onResult(List<Customer> customers) {
                for (Customer customer : customers) {
                    addCustomersMarker(new GeoPoint(customer.getLatitude(), customer.getLongitude()), customer.getCustomerName());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                showToastMessage(throwable.getMessage());
            }
        });
    }

    private void fetchAllLocationsAndDrawRoutes() {
        locationService.getAllLocations(new ResultListener<List<LocationObj>>() {
            @Override
            public void onResult(List<LocationObj> locations) {
                groupLocationsByUserId(locations);
            }

            @Override
            public void onError(Throwable throwable) {
                showToastMessage(getString(R.string.Error_fetching_users) + throwable.getMessage());
            }
        });
    }

    private void groupLocationsByUserId(List<LocationObj> locations) {
        Map<String, List<GeoPoint>> groupedLocations = new HashMap<>();

        for (LocationObj location : locations) {
            if (!groupedLocations.containsKey(location.getUserId())) {
                groupedLocations.put(location.getUserId(), new ArrayList<>());
            }
            groupedLocations.get(location.getUserId()).add(new GeoPoint(location.getLatitude(), location.getLongitude()));
        }

        for (String userId : groupedLocations.keySet()) {
            List<GeoPoint> route = groupedLocations.get(userId);

            if (!userColors.containsKey(userId)) {
                userColors.put(userId, getRandomColor());
            }
            int color = userColors.get(userId);

            drawUsersRoute(route, color);
        }
    }


    private void startMarketerAutoUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userRoute();
                markUserCustomersLocations();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private void startCollectionAutoUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userRoute();
                markCustomersLocations();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    private void startAdminAutoUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchAllLocationsAndDrawRoutes();
                markCustomersLocations();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map.onDetach();
            map = null;
        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

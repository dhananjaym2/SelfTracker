package sample.self.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import sample.self.tracker.data.db.UserLocation;
import sample.self.tracker.utils.AppUtils;
import sample.self.tracker.utils.DialogButtonCallBack;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

  private static final String TAG = MapActivity.class.getSimpleName();
  private GoogleMap googleMap;
  private Circle circle;
  private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
  private List<Marker> listOfMarkers = new ArrayList<>();
  private LatLngBounds.Builder markerBounds = new LatLngBounds.Builder();
  private FusedLocationProviderClient fusedLocationProviderClient;
  private LocationCallback locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult locationResult) {
      Location location = locationResult.getLastLocation();
      if (location != null) {
        Log.d(TAG, "onLocationResult() lat: " + location.getLatitude() + " lng:"
            + location.getLongitude());
        //NotificationUtils.showNotificationForLocation(MapActivity.this, location);
        saveUserLocation(location);
      } else {
        Log.e(TAG, "location null in onLocationResult()");
      }
    }

    private void saveUserLocation(Location locationToSave) {
      UserLocation userLocation = new UserLocation();
      userLocation.setLat(locationToSave.getLatitude());
      userLocation.setLng(locationToSave.getLongitude());
      userLocation.setTime(locationToSave.getTime());
      userLocationViewModel.insert(userLocation);
    }
  };

  /**
   * Provides the entry point to the Fused Location Provider API.
   */
  private FusedLocationProviderClient mFusedLocationClient;

  /**
   * Represents a geographical location.
   */
  protected Location mLastLocation;

  private UserLocationViewModel userLocationViewModel;
  private final int REQUEST_CODE_RESOLVE_LOCATION = 823;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    if (AppUtils.isGooglePlayServicesAvailable(this)) {
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          .findFragmentById(R.id.googleMapFragment);
      mapFragment.getMapAsync(this);
    } else {
      AppUtils.showAlertMessage(this, getString(R.string.installGooglePlayServices),
          new DialogButtonCallBack() {
            @Override public void onDialogButtonClick(Intent intent) {
              finish();
            }
          });
    }

    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    userLocationViewModel = new ViewModelProvider(this).get(UserLocationViewModel.class);
  }

  /**
   * Add and show circle near the specific LatLng
   *
   * @param googleMap GoogleMap
   * @param latLng LatLng
   */
  private void showCircle(GoogleMap googleMap, LatLng latLng) {
    if (googleMap == null) {
      Log.e(TAG, "googleMap is null, can't show circle");
      return;
    }
    if (circle != null) {// remove previously shown circle
      circle.remove();
    }
    CircleOptions circleOptions = new CircleOptions()
        .center(latLng)
        .radius(100)
        .strokeWidth(4f)
        .strokeColor(Color.RED)
        .visible(true);
    circle = googleMap.addCircle(circleOptions);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (fusedLocationProviderClient != null && locationCallback != null) {
      fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;

    addObserverForUserLocation();

    checkPermissions();
  }

  private void checkPermissions() {
    if (!isPermissionGranted()) {
      requestPermissions();
    } else {
      startLocationTracking();
    }
  }

  private void addObserverForUserLocation() {
    userLocationViewModel.getLastUserLocationData().observe(this,
        new Observer<List<UserLocation>>() {
          @Override public void onChanged(final List<UserLocation> userLocations) {

            for (Marker marker : listOfMarkers) {// remove any previous markers first
              marker.remove();
            }

            for (int index = 0; index < userLocations.size(); index++) {
              UserLocation userLocation = userLocations.get(index);

              LatLng latLng = new LatLng(userLocation.getLat(), userLocation.getLng());

              // to show date time as the marker title.
              DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
              format.setTimeZone(TimeZone.getDefault());
              String dateInString = format.format(new Date(userLocation.getTime()));

              MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                  .title(dateInString);
              Marker marker = MapActivity.this.googleMap.addMarker(markerOptions);
              listOfMarkers.add(marker);
              markerBounds.include(latLng);
            }

            if (userLocations.size() > 0) {
              // show circle near the last/latest location
              showCircle(MapActivity.this.googleMap,
                  new LatLng(userLocations.get(0).getLat(), userLocations.get(0).getLng()));

              LatLngBounds bounds = markerBounds.build();
              MapActivity.this.googleMap.animateCamera(
                  CameraUpdateFactory.newLatLngBounds(bounds, 50));
            }
          }
        });
  }

  private void startLocationTracking() {
    LocationRequest locationRequest = new LocationRequest();
    final long INTERVAL_LOCATION_UPDATES = 30000;// in milli seconds
    locationRequest.setInterval(INTERVAL_LOCATION_UPDATES);
    locationRequest.setFastestInterval(INTERVAL_LOCATION_UPDATES);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(this);
    int locationPermission =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (locationPermission == PackageManager.PERMISSION_GRANTED) {

      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
          .addLocationRequest(locationRequest);
      LocationSettingsRequest locationSettingsRequest = builder.build();
      builder.setAlwaysShow(true);
      SettingsClient settingsClient = LocationServices.getSettingsClient(this);

      // Check if location is enabled or not.
      settingsClient
          .checkLocationSettings(locationSettingsRequest)
          .addOnSuccessListener(this,
              new OnSuccessListener<LocationSettingsResponse>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                  Log.i(TAG, "Location is enabled");
                }
              })
          .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              int statusCode = ((ApiException) e).getStatusCode();
              switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                  try {
                    // Show the dialog by calling startResolutionForResult(), and check the
                    // result in onActivityResult().
                    ResolvableApiException rae = (ResolvableApiException) e;
                    rae.startResolutionForResult(MapActivity.this,
                        REQUEST_CODE_RESOLVE_LOCATION);
                  } catch (IntentSender.SendIntentException sie) {
                    sie.printStackTrace();
                  }
                  break;

                default:
                  AppUtils.showAlertMessage(MapActivity.this,
                      "Location settings are inadequate, and cannot be fixed here. " +
                          "Please fix it in Settings.", null);
                  break;
              }
            }
          });

      fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
  }

  /**
   * Provides a simple way of getting a device's location and is well suited for
   * applications that do not require a fine-grained location and that do not need location
   * updates. Gets the best and most recent location currently available, which may be null
   * in rare cases when a location is not available.
   * <p>
   * Note: this method should be called after location permission has been granted.
   */
  @SuppressWarnings("MissingPermission")
  private void getLastLocation() {
    mFusedLocationClient.getLastLocation()
        .addOnCompleteListener(this, new OnCompleteListener<Location>() {
          @Override
          public void onComplete(@NonNull Task<Location> task) {
            if (task.isSuccessful() && task.getResult() != null) {
              mLastLocation = task.getResult();
              Log.v(TAG, "loc: " + mLastLocation.getLatitude() + mLastLocation.getLongitude());
              Toast.makeText(MapActivity.this,
                  "loc: " + mLastLocation.getLatitude() + mLastLocation.getLongitude(),
                  Toast.LENGTH_LONG).show();
            } else {
              Log.w(TAG, "getLastLocation:exception", task.getException());
              AppUtils.showAlertMessage(MapActivity.this, getString(R.string.no_location_detected),
                  null);
            }
          }
        });
  }

  /**
   * Return the current state of the permissions needed.
   */
  private boolean isPermissionGranted() {
    int permissionState = ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION);
    return permissionState == PackageManager.PERMISSION_GRANTED;
  }

  private void startLocationPermissionRequest() {
    ActivityCompat.requestPermissions(MapActivity.this,
        new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
        REQUEST_PERMISSIONS_REQUEST_CODE);
  }

  private void requestPermissions() {
    boolean shouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.ACCESS_FINE_LOCATION);

    // Provide an additional rationale to the user. This would happen if the user denied the
    // request previously, but didn't check the "Don't ask again" checkbox.
    if (shouldProvideRationale) {
      Log.i(TAG, "Displaying permission rationale to provide additional context.");

      AppUtils.showAlertMessageWith2Buttons(this, getString(R.string.permission_rationale),
          getString(R.string.grantPermission), getString(android.R.string.cancel),
          new DialogButtonCallBack() {
            @Override public void onDialogButtonClick(Intent intent) {
              if (intent.getBooleanExtra(DialogButtonCallBack.clickedValue, false)) {
                // Request permission
                startLocationPermissionRequest();
              } else {
                // user clicked cancel in rationale.
                checkPermissions();
              }
            }
          });
    } else {
      Log.i(TAG, "Requesting permission");
      // Request permission. It's possible this can be auto answered if device policy
      // sets the permission in a given state or the user denied the permission
      // previously and checked "Never ask again".
      startLocationPermissionRequest();
    }
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    Log.i(TAG, "onRequestPermissionResult");
    if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length <= 0) {
        // If user interaction was interrupted, the permission request is cancelled and you
        // receive empty arrays.
        Log.i(TAG, "User interaction was cancelled.");
      } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission granted.
        getLastLocation();
      } else {
        // Permission denied.

        // Notify the user that they have rejected a core permission for the app, which makes the
        // Activity useless.

        AppUtils.showAlertMessageWith2Buttons(this,
            getString(R.string.permission_denied_explanation),
            getString(R.string.settings), getString(android.R.string.cancel),
            new DialogButtonCallBack() {
              @Override public void onDialogButtonClick(Intent intent) {
                if (intent.getBooleanExtra(DialogButtonCallBack.clickedValue, false)) {

                  // Build intent that displays the App settings screen.
                  Intent intentForAppSettings = new Intent();
                  intentForAppSettings.setAction(
                      Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                  Uri uri = Uri.fromParts("package",
                      BuildConfig.APPLICATION_ID, null);
                  intentForAppSettings.setData(uri);
                  intentForAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intentForAppSettings);
                } else {
                  // user clicked cancel in rationale.
                  checkPermissions();
                }
              }
            });
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.v(TAG, "in onActivityResult() requestCode:" + REQUEST_CODE_RESOLVE_LOCATION);
    switch (requestCode) {
      case REQUEST_CODE_RESOLVE_LOCATION:
        startLocationTracking();
        break;
    }
  }
}

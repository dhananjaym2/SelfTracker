package sample.self.tracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import sample.self.tracker.utils.AppUtils;
import sample.self.tracker.utils.DialogButtonCallBack;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

  private GoogleMap googleMap;
  private CircleOptions circle;
  private LatLng currentLatLong;

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
      AppUtils.showErrorMessage(this, getString(R.string.installGooglePlayServices),
          new DialogButtonCallBack() {
            @Override public void onDialogButtonClick(Intent intent) {
              finish();
            }
          });
    }

    //TODO check location permission
    // start tracking and Save locations.
  }

  private void showCurrentLocationOnMap() {
    // TODO get current location
    currentLatLong = new LatLng(17.0991897, 81.7705551);
    this.googleMap.addMarker(
        new MarkerOptions().position(currentLatLong).title(getString(R.string.weAreHere)));
    googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));
    addCircle(googleMap, currentLatLong);
  }

  private void addCircle(GoogleMap googleMap, LatLng latLng) {
    circle = new CircleOptions()
        .center(latLng)
        .radius(100)
        .strokeWidth(4f)
        .strokeColor(Color.RED);
    googleMap.addCircle(circle);
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    this.googleMap = googleMap;
    //googleMap.setMyLocationEnabled(true);

    // Add a marker in Sydney and move the camera
    //LatLng sydney = new LatLng(-34, 151);
    //this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    //this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    showCurrentLocationOnMap();
  }
}

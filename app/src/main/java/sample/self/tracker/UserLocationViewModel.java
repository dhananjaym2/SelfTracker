package sample.self.tracker;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import sample.self.tracker.data.UserLocationRepository;
import sample.self.tracker.data.db.UserLocation;

public class UserLocationViewModel extends AndroidViewModel {

  private UserLocationRepository userLocationRepository;

  private LiveData<List<UserLocation>> lastUserLocationData;

  public UserLocationViewModel(@NonNull Application application) {
    super(application);
    userLocationRepository = new UserLocationRepository(application);
    lastUserLocationData = userLocationRepository.getLastUserLocationData();
  }

  LiveData<List<UserLocation>> getLastUserLocationData() {
    return lastUserLocationData;
  }

  public void insert(UserLocation userLocation) {
    userLocationRepository.insert(userLocation);
  }
}

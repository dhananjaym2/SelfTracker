package sample.self.tracker.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import sample.self.tracker.data.db.AppDatabase;
import sample.self.tracker.data.db.UserLocation;
import sample.self.tracker.data.db.UserLocationDao;

public class UserLocationRepository {

  private UserLocationDao userLocationDao;
  private LiveData<List<UserLocation>> lastUserLocationData;

  public UserLocationRepository(Application application) {
    AppDatabase database = AppDatabase.getDatabase(application);
    userLocationDao = database.userLocationDao();
    lastUserLocationData = userLocationDao.getLastUserLocations(5);
  }

  public LiveData<List<UserLocation>> getLastUserLocationData() {
    return lastUserLocationData;
  }

  public void insert(final UserLocation userLocation) {
    AppDatabase.databaseWriteExecutor.execute(new Runnable() {
      @Override public void run() {
        userLocationDao.insert(userLocation);
      }
    });
  }
}

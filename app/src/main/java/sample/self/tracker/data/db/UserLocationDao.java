package sample.self.tracker.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UserLocationDao {

  @Insert
  void insert(UserLocation userLocation);

  @Query("SELECT * FROM USER_LOCATION ORDER BY id DESC LIMIT :limitCountOfRecords")
  LiveData<List<UserLocation>> getLastUserLocations(int limitCountOfRecords);
}
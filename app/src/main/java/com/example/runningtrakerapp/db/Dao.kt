package com.example.runningtrakerapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    //  filter for sorting
    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistanceInMeters(): Flow<List<Run>>

    //  getting Statistics
    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): Flow<Long>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalDistanceInMeters(): Flow<Int>

    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalCaloriesBurned(): Flow<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeedInKMH(): Flow<Float>

    @Query("""SELECT SUM(distanceInMeters) 
    FROM running_table 
    WHERE timestamp >= :startOfWeek AND timestamp <= :endOfWeek
""")
    fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long): Flow<Int>

// @Query("""
//    SELECT SUM(distanceInMeters)
//    FROM running_table
//    WHERE timestamp BETWEEN :startOfWeek AND :endOfWeek
//""")
//    fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long): Flow<Int>


}





     /*
     Using the provided code, you will be able to get the total distance covered for the current week and the previous week based on the timestamps calculated. Here's a breakdown of what you should expect as output and how it will be used:

Expected Output
Distance Covered in the Current Week:

This will be the total distance (distanceInMeters) covered from Monday of the current week to Sunday of the current week.
For example, if today is Wednesday, and you query for the current week, the query will sum up all distanceInMeters entries from the previous Monday to the upcoming Sunday.
Distance Covered in the Previous Week:

This will be the total distance (distanceInMeters) covered from Monday of the previous week to Sunday of the previous week.
For example, if today is Wednesday, and you query for the previous week, the query will sum up all distanceInMeters entries from the Monday of the week before the current week to the Sunday before the current week.
How It Works
DAO Queries:

kotlin
Copy code
@Query("""
    SELECT SUM(distanceInMeters)
    FROM running_table
    WHERE timestamp BETWEEN :startOfWeek AND :endOfWeek
""")
fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long): Flow<Int>
This query will return the total distance covered between the startOfWeek and endOfWeek timestamps. It uses the timestamp field from your Run entity to filter records.

Week Calculation:

kotlin
Copy code
fun getStartAndEndOfWeek(weekOffset: Int): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startOfWeek = calendar.timeInMillis
    calendar.add(Calendar.WEEK_OF_YEAR, 1)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val endOfWeek = calendar.timeInMillis + 24 * 60 * 60 * 1000 - 1
    return Pair(startOfWeek, endOfWeek)
}
For weekOffset = 0 (current week), startOfWeek will be the timestamp for the Monday of the current week and endOfWeek will be the timestamp for the Sunday of the current week.
For weekOffset = -1 (previous week), startOfWeek will be the timestamp for the Monday of the previous week and endOfWeek will be the timestamp for the Sunday of the previous week.
Example
Let's say today is Wednesday, September 18, 2024:

Current Week Calculation (Week Offset = 0):

Monday: September 16, 2024
Sunday: September 22, 2024
The getTotalDistanceForCurrentWeek() method will query for distances recorded between September 16, 2024, and September 22, 2024.

Previous Week Calculation (Week Offset = -1):

Monday: September 9, 2024
Sunday: September 15, 2024
The getTotalDistanceForPreviousWeek() method will query for distances recorded between September 9, 2024, and September 15, 2024.

Final Output
When you observe totalDistanceCurrentWeek and totalDistancePreviousWeek in your UI, you will get:

Total distance for the current week: This is the sum of all distances (distanceInMeters) recorded from the Monday of the current week to the Sunday of the current week.
Total distance for the previous week: This is the sum of all distances (distanceInMeters) recorded from the Monday of the previous week to the Sunday of the previous week.
These values will be dynamically updated based on the data in your running_table and will be displayed in your UI according to the LiveData observations.




To implement a weekly goal feature where a week starts on Monday and ends on Sunday, and you need to track the distanceInMeters covered in the current week and the previous week, you can follow these steps:

Define the Week Boundaries: Determine the start and end timestamps for both the current week and the previous week based on the Monday-to-Sunday week.

Create DAO Queries: Write queries to fetch the total distance covered during these weeks.

Calculate Week Boundaries: Implement methods to calculate the start and end timestamps for the weeks.

Fetch and Observe Data: Use the queries in your ViewModel or Repository to get the required data and observe it in your UI.

Here’s how you can implement this:

1. Define DAO Queries
Add queries in your Dao interface to get the total distance for the specified week boundaries:

kotlin
Copy code
@Dao
interface Dao {

    @Query("""
        SELECT SUM(distanceInMeters)
        FROM running_table
        WHERE timestamp BETWEEN :startOfWeek AND :endOfWeek
    """)
    fun getTotalDistanceForWeek(startOfWeek: Long, endOfWeek: Long): Flow<Int>

}
2. Calculate Week Boundaries
Implement methods to get the start and end timestamps for the current week and the previous week. Here’s how you can do it:

kotlin
Copy code
import java.util.Calendar

fun getStartAndEndOfWeek(weekOffset: Int): Pair<Long, Long> {
    val calendar = Calendar.getInstance()

    // Move to the start of the week based on the given offset
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    // Set the calendar to Monday (first day of the week)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startOfWeek = calendar.timeInMillis

    // Move to Sunday of the same week
    calendar.add(Calendar.WEEK_OF_YEAR, 1)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val endOfWeek = calendar.timeInMillis + 24 * 60 * 60 * 1000 - 1 // End of Sunday

    return Pair(startOfWeek, endOfWeek)
}
weekOffset of 0 will get the current week.
weekOffset of -1 will get the previous week.
3. Use the Queries in Your Repository
Use the calculated timestamps in your repository to fetch the data:

kotlin
Copy code
class RunRepository(private val dao: Dao) {

    fun getTotalDistanceForCurrentWeek(): Flow<Int> {
        val (startOfWeek, endOfWeek) = getStartAndEndOfWeek(0)
        return dao.getTotalDistanceForWeek(startOfWeek, endOfWeek)
    }

    fun getTotalDistanceForPreviousWeek(): Flow<Int> {
        val (startOfWeek, endOfWeek) = getStartAndEndOfWeek(-1)
        return dao.getTotalDistanceForWeek(startOfWeek, endOfWeek)
    }
}
4. Observe Data in Your ViewModel and UI
In your ViewModel, expose the total distance for the current and previous weeks:

kotlin
Copy code
class RunViewModel(private val repository: RunRepository) : ViewModel() {

    val totalDistanceCurrentWeek: LiveData<Int> = repository.getTotalDistanceForCurrentWeek()
        .asLiveData()

    val totalDistancePreviousWeek: LiveData<Int> = repository.getTotalDistanceForPreviousWeek()
        .asLiveData()
}
In your Activity or Fragment, observe these LiveData properties:

kotlin
Copy code
class RunFragment : Fragment() {

    private lateinit var viewModel: RunViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_run, container, false)

        viewModel = ViewModelProvider(this).get(RunViewModel::class.java)

        viewModel.totalDistanceCurrentWeek.observe(viewLifecycleOwner, Observer { distance ->
            // Update UI with total distance for the current week
        })

        viewModel.totalDistancePreviousWeek.observe(viewLifecycleOwner, Observer { distance ->
            // Update UI with total distance for the previous week
        })

        return view
    }
}
Summary
Define queries in your Dao to calculate total distance for a given week.
Calculate week boundaries using Calendar to get start and end timestamps for the current and previous weeks.
Fetch data in your repository using these timestamps.
Observe data in your UI to update the display based on the current and previous week’s distances.
This setup will help you track and display the distance covered in the current and previous weeks in your running tracker app.





      */




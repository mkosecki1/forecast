package com.example.forecastmvvm.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecastmvvm.data.db.entity.FutureWeatherEntry
import com.example.forecastmvvm.data.db.unitlocalized.future.detail.ImperialDetailFutureWeatherEntry
import com.example.forecastmvvm.data.db.unitlocalized.future.detail.MetricDetailFutureWeatherEntry
import com.example.forecastmvvm.data.db.unitlocalized.future.list.ImperialSimpleFutureWeatherEntry
import com.example.forecastmvvm.data.db.unitlocalized.future.list.MetricSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate

@Dao
interface FutureWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(futureWeatherEntries: List<FutureWeatherEntry>)

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastsMetric(startDate: LocalDate): LiveData<List<MetricSimpleFutureWeatherEntry>>

    @Query("select * from future_weather where date(date) >= date(:startDate)")
    fun getSimpleWeatherForecastsImperial(startDate: LocalDate): LiveData<List<ImperialSimpleFutureWeatherEntry>>

    @Query("select count(id) from future_weather where date(date) >= date(:startDate)")
    fun countFutureWeather(startDate: LocalDate): Int

    @Query("delete from future_weather where date(date) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)

    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailWeatherForecastsMetric(date: LocalDate): LiveData<MetricDetailFutureWeatherEntry>

    @Query("select * from future_weather where date(date) = date(:date)")
    fun getDetailWeatherForecastsImperial(date: LocalDate): LiveData<ImperialDetailFutureWeatherEntry>
}
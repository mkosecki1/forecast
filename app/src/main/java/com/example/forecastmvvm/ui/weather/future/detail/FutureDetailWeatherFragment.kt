package com.example.forecastmvvm.ui.weather.future.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.forecastmvvm.R.layout
import com.example.forecastmvvm.data.db.LocalDateConverter
import com.example.forecastmvvm.internal.DateNotFoundException
import com.example.forecastmvvm.internal.glide.GlideApp
import com.example.forecastmvvm.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.future_detail_weather_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.factory
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureDetailWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by kodein()
    private lateinit var viewModel: FutureDetailWeatherViewModel
    private val viewModelFactoryInstanceFactory: ((LocalDate) -> FutureDetailWeatherViewModelFactory) by factory()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            layout.future_detail_weather_fragment,
            container,
            false
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val safeArgs = arguments?.let { FutureDetailWeatherFragmentArgs.fromBundle(it) }
        val date =
            LocalDateConverter.stringToDate(safeArgs?.dateString) ?: throw DateNotFoundException()
        viewModel = ViewModelProviders.of(this, viewModelFactoryInstanceFactory(date))
            .get(FutureDetailWeatherViewModel::class.java)
        bindUI()
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val futureWeather = viewModel.weather.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(this@FutureDetailWeatherFragment, Observer {
            if (it == null) return@Observer
            updateLocation(it.name)
        })

        futureWeather.observe(this@FutureDetailWeatherFragment, Observer {
            if (it == null) return@Observer
            group_loading_detail.visibility = View.GONE
            updateDate(it.date)
            updateTemperatures(it.avgTemperature, it.minTemperature, it.maxTemperature)
            updateCondition(it.conditionText)
            updatePrecipitation(it.totalPrecipitation)
            updateWind(it.maxWindSpeed)
            updateVisibility(it.avgVisibilityDistance)
            GlideApp.with(this@FutureDetailWeatherFragment)
                .load("http:${it.conditionIconUrl}")
                .into(imageView_condition_icon_detail)
        })
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedAbbreviation("km", "mi.")
        textView_visibility_detail.text = "Widoczność: $visibilityDistance $unitAbbreviation"
    }

    private fun updateWind(windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedAbbreviation("km/h", "mph")
        textView_wind_speed_detail.text = "Prędkość wiatru: $windSpeed $unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        textView_condition_detail.text = condition
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updatePrecipitation(totalPrecipitation: Double) {
        val unitAbbreviation = chooseLocalizedAbbreviation("mm", "in")
        textView_precipitation_detail.text =
            "Opady atmosferyczne: $totalPrecipitation $unitAbbreviation"
    }

    private fun updateTemperatures(
        temperature: Double,
        minTemperature: Double,
        maxTemperature: Double
    ) {
        val unitAbbreviation = chooseLocalizedAbbreviation("°C", "°F")
        textView_temperature_detail.text = "$temperature$unitAbbreviation"
        textView_min_max_temperature_detail.text =
            "Temperatura min: $minTemperature$unitAbbreviation / max: $maxTemperature$unitAbbreviation"
    }

    private fun chooseLocalizedAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetricUnit) metric else imperial

    }

    private fun updateDate(date: LocalDate) {
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle =
            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        textView_date_day_details.text =
            LocalDateConverter.weekDaysConverter(date.dayOfWeek.toString())
    }
}
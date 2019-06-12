package com.example.forecastmvvm.ui.weather.future.list

import android.annotation.SuppressLint
import com.example.forecastmvvm.R
import com.example.forecastmvvm.data.db.LocalDateConverter
import com.example.forecastmvvm.data.db.unitlocalized.future.list.MetricSimpleFutureWeatherEntry
import com.example.forecastmvvm.data.db.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.example.forecastmvvm.internal.glide.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_future_weather.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureWeatherItem(
    val weatherEntry: UnitSpecificSimpleFutureWeatherEntry
) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            textView_condition.text = weatherEntry.conditionText
            updateDate()
            updateTemperature()
            updateConditionImage()
        }
    }

    override fun getLayout() = R.layout.item_future_weather

    @SuppressLint("SimpleDateFormat")
    private fun ViewHolder.updateDate() {
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        textView_date.text = weatherEntry.date.format(dtFormatter)
        textView_date_day.text =
            LocalDateConverter.weekDaysConverter(weatherEntry.date.dayOfWeek.toString())
    }

    private fun ViewHolder.updateTemperature() {
        val unitAbbereviation = if (weatherEntry is MetricSimpleFutureWeatherEntry) "°C"
        else "°F"
        textView_temperature.text = "${weatherEntry.avgTemperature}$unitAbbereviation"
    }

    private fun ViewHolder.updateConditionImage() {
        GlideApp.with(this.containerView).load("http:" + weatherEntry.conditionIconUrl)
            .into(imageView_condition_icon)
    }
}
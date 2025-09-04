package org.jetbrains.plugins.template.weatherApp.model

import org.jetbrains.jewel.ui.icon.IconKey
import org.jetbrains.plugins.template.weatherApp.ui.WeatherIcons
import java.time.LocalDateTime

/**
 * Data class representing a daily weather forecast.
 */
data class DailyForecast(
    val date: LocalDateTime,
    val temperature: Float,
    val weatherType: WeatherType,
    val humidity: Int,
    val windSpeed: Float,
    val windDirection: WindDirection
)

/**
 * Data class representing weather information to be displayed in the Weather Card.
 */
data class WeatherForecastData(
    val location: Location,
    val currentWeatherForecast: DailyForecast,
    val dailyForecasts: List<DailyForecast> = emptyList()
) {
    companion object {
        val EMPTY: WeatherForecastData = WeatherForecastData(
            Location("", ""),
            DailyForecast(
                LocalDateTime.now(),
                0f,
                WeatherType.CLEAR,
                0,
                0f,
                WindDirection.NORTH,
            ),
            emptyList()
        )
    }
}

/**
 * Enum representing different weather types.
 */
enum class WeatherType(val label: String, val dayIconKey: IconKey, val nightIconKey: IconKey) {
    CLEAR("Sunny", WeatherIcons.SUNNY, WeatherIcons.SUNNY),
    CLOUDY("Cloudy", dayIconKey = WeatherIcons.CLOUDY, nightIconKey = WeatherIcons.CLOUDY),
    PARTLY_CLOUDY(
        "Partly Cloudy",
        dayIconKey = WeatherIcons.PARTLY_CLOUDY,
        nightIconKey = WeatherIcons.PARTLY_CLOUDY,
    ),
    RAINY("Rainy", dayIconKey = WeatherIcons.RAINY, nightIconKey = WeatherIcons.RAINY),
    RAINY_AND_THUNDER(
        "Rainy and Thunder",
        dayIconKey = WeatherIcons.STORM,
        nightIconKey = WeatherIcons.STORM
    ),
    THUNDER("Thunder", dayIconKey = WeatherIcons.STORM, nightIconKey = WeatherIcons.STORM),
    SNOWY("Snowy", dayIconKey = WeatherIcons.SNOWY, nightIconKey = WeatherIcons.SNOWY);

    companion object {
        fun random(): WeatherType = entries.toTypedArray().random()
    }
}

/**
 * Enum representing wind directions.
 */
enum class WindDirection(val label: String) {
    NORTH("↑"),
    NORTH_EAST("↗"),
    EAST("→"),
    SOUTH_EAST("↘"),
    SOUTH("↓"),
    SOUTH_WEST("↙"),
    WEST("←"),
    NORTH_WEST("↖");

    companion object {
        fun random(): WindDirection = entries.toTypedArray().random()
    }
}
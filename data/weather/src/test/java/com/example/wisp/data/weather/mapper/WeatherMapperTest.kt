package com.example.wisp.data.weather.mapper

import com.example.wisp.data.weather.dto.CurrentWeatherDto
import com.example.wisp.data.weather.dto.ForecastDto
import com.example.wisp.data.weather.dto.PlaceSearchDto
import com.example.wisp.domain.model.Place
import com.example.wisp.domain.model.WeatherBundle
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class WeatherMapperTest {
    
    private lateinit var weatherMapper: WeatherMapper
    private val json = Json { ignoreUnknownKeys = true }
    
    @Before
    fun setUp() {
        weatherMapper = WeatherMapper()
    }
    
    @Test
    fun `map current weather DTO to domain model`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("current_weather_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dto = json.decodeFromString<CurrentWeatherDto>(jsonString)
        val weatherNow = weatherMapper.mapToWeatherNow(dto)
        
        assertEquals(22.5, weatherNow.tempC, 0.001)
        assertEquals(72.5, weatherNow.tempF, 0.001) // 22.5 * 9/5 + 32
        assertEquals("Clear sky", weatherNow.condition)
        assertEquals("01d", weatherNow.icon)
        assertEquals(65, weatherNow.humidity)
        assertEquals(11.52, weatherNow.windKph, 0.001) // 3.2 * 3.6
        assertEquals(24.1, weatherNow.feelsLikeC, 0.001)
        assertEquals(1695123456L, weatherNow.dt)
    }
    
    @Test
    fun `map forecast DTOs to hourly weather models`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("forecast_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dto = json.decodeFromString<ForecastDto>(jsonString)
        val hourlyWeather = weatherMapper.mapToHourlyWeather(dto.forecastList)
        
        assertEquals(3, hourlyWeather.size)
        
        // Verify first hourly item
        val firstHourly = hourlyWeather[0]
        assertEquals(1695123456L, firstHourly.dt)
        assertEquals(22.5, firstHourly.tempC, 0.001)
        assertEquals(72.5, firstHourly.tempF, 0.001)
        assertEquals("01d", firstHourly.icon)
        assertEquals(0.0, firstHourly.precipMm, 0.001)
        
        // Verify second hourly item with rain
        val secondHourly = hourlyWeather[1]
        assertEquals(1695134256L, secondHourly.dt)
        assertEquals(20.1, secondHourly.tempC, 0.001)
        assertEquals(68.18, secondHourly.tempF, 0.001)
        assertEquals("02n", secondHourly.icon)
        assertEquals(0.5, secondHourly.precipMm, 0.001)
        
        // Verify third hourly item with more rain
        val thirdHourly = hourlyWeather[2]
        assertEquals(1695145056L, thirdHourly.dt)
        assertEquals(18.5, thirdHourly.tempC, 0.001)
        assertEquals(65.3, thirdHourly.tempF, 0.001)
        assertEquals("10n", thirdHourly.icon)
        assertEquals(2.1, thirdHourly.precipMm, 0.001)
    }
    
    @Test
    fun `map forecast DTOs to daily weather models`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("forecast_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dto = json.decodeFromString<ForecastDto>(jsonString)
        val dailyWeather = weatherMapper.mapToDailyWeather(dto.forecastList)
        
        // Should have at least one day
        assert(dailyWeather.isNotEmpty())
        
        // Verify first daily item
        val firstDaily = dailyWeather[0]
        assertEquals(1695123456L, firstDaily.dt)
        assertEquals(18.5, firstDaily.minC, 0.001) // Min from all items
        assertEquals(22.5, firstDaily.maxC, 0.001) // Max from all items
        assertEquals(65.3, firstDaily.minF, 0.001) // 18.5 * 9/5 + 32
        assertEquals(72.5, firstDaily.maxF, 0.001) // 22.5 * 9/5 + 32
        assertEquals("02n", firstDaily.icon) // Middle item's icon
    }
    
    @Test
    fun `map place search DTO to domain model`() {
        val jsonString = javaClass.classLoader
            ?.getResourceAsStream("place_search_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load test JSON file")
        
        val dtoList = json.decodeFromString<List<PlaceSearchDto>>(jsonString)
        val places = dtoList.map { weatherMapper.mapToPlace(it) }
        
        assertEquals(3, places.size)
        
        // Verify first place
        val firstPlace = places[0]
        assertEquals("40.7128_-74.006", firstPlace.id)
        assertEquals("New York, New York, US", firstPlace.name)
        assertEquals(40.7128, firstPlace.lat, 0.001)
        assertEquals(-74.006, firstPlace.lon, 0.001)
        
        // Verify second place
        val secondPlace = places[1]
        assertEquals("40.7589_-73.9851", secondPlace.id)
        assertEquals("New York, New York, US", secondPlace.name)
        assertEquals(40.7589, secondPlace.lat, 0.001)
        assertEquals(-73.9851, secondPlace.lon, 0.001)
        
        // Verify third place
        val thirdPlace = places[2]
        assertEquals("46.5181_-95.3761", thirdPlace.id)
        assertEquals("New York Mills, Minnesota, US", thirdPlace.name)
        assertEquals(46.5181, thirdPlace.lat, 0.001)
        assertEquals(-95.3761, thirdPlace.lon, 0.001)
    }
    
    @Test
    fun `map complete weather bundle from current weather and forecast`() {
        val currentWeatherJson = javaClass.classLoader
            ?.getResourceAsStream("current_weather_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load current weather JSON file")
        
        val forecastJson = javaClass.classLoader
            ?.getResourceAsStream("forecast_response.json")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: throw AssertionError("Could not load forecast JSON file")
        
        val currentWeatherDto = json.decodeFromString<CurrentWeatherDto>(currentWeatherJson)
        val forecastDto = json.decodeFromString<ForecastDto>(forecastJson)
        
        val weatherBundle = weatherMapper.mapToWeatherBundle(currentWeatherDto, forecastDto)
        
        // Verify place
        assertEquals("40.7143_-74.006", weatherBundle.place.id)
        assertEquals("New York", weatherBundle.place.name)
        assertEquals(40.7143, weatherBundle.place.lat, 0.001)
        assertEquals(-74.006, weatherBundle.place.lon, 0.001)
        
        // Verify current weather
        assertEquals(22.5, weatherBundle.now.tempC, 0.001)
        assertEquals("Clear sky", weatherBundle.now.condition)
        
        // Verify hourly forecast
        assertEquals(3, weatherBundle.hourly.size)
        
        // Verify daily forecast
        assert(weatherBundle.daily.isNotEmpty())
    }
}



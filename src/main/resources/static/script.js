document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const cityName = urlParams.get('city');
    const lat = urlParams.get('lat');
    const lon = urlParams.get('lon');
    const isLocation = urlParams.get('location');
    
    if (cityName) {
        fetchWeatherData(cityName);
    } else if (lat && lon && isLocation) {
        fetchWeatherByCoordinates(lat, lon);
    } else {
        showError('No city or location specified');
    }
});

function goHome() {
    window.location.href = 'index.html';
}

async function fetchWeatherData(cityName) {
    try {
        showLoading();
        const response = await fetch(`/api/weather/${encodeURIComponent(cityName)}`);
        
        if (response.ok) {
            const weatherData = await response.json();
            displayWeatherData(weatherData);
        } else {
            const errorText = await response.text();
            if (errorText && errorText.trim()) {
                showError(errorText);
            } else {
                // fallback
                switch(response.status) {
                    case 404:
                        showError('City not found. Please check the spelling and try again.');
                        break;
                    case 401:
                        showError('Weather service temporarily unavailable.');
                        break;
                    case 503:
                        showError('Network error - please try again later.');
                        break;
                    default:
                        showError('Something went wrong. Please try again.');
                }
            }
        }
    } catch (error) {
        console.error('Error fetching weather data:', error);
        showError('Failed to fetch weather data. Please try again.');
    }
}

async function fetchWeatherByCoordinates(lat, lon) {
    try {
        showLoading();
        const response = await fetch(`/api/weather/coordinates?lat=${lat}&lon=${lon}`);
        
        if (response.ok) {
            const weatherData = await response.json();
            displayWeatherData(weatherData);
        } else {
            const errorText = await response.text();
            if (errorText && errorText.trim()) {
                showError(errorText);
            } else {
                // fallback 
                switch(response.status) {
                    case 404:
                        showError('Weather data not available for your location.');
                        break;
                    case 401:
                        showError('Weather service temporarily unavailable.');
                        break;
                    case 503:
                        showError('Network error - please try again later.');
                        break;
                    default:
                        showError('Something went wrong. Please try again.');
                }
            }
        }
    } catch (error) {
        console.error('Error fetching weather by coordinates:', error);
        showError('Failed to fetch weather data for your location. Please try again.');
    }
}

function displayWeatherData(data) {
    hideLoading();
    
    document.getElementById('city-name').textContent = data.city;
    
    document.getElementById('temperature').textContent = data.temperature;
    
    document.getElementById('description').textContent = data.description;
    
    document.getElementById('feels-like').textContent = data.feelsLike;
    document.getElementById('humidity').textContent = data.humidity;
    document.getElementById('wind-speed').textContent = data.windSpeed;
    
    document.getElementById('weather-results').style.display = 'block';
    document.getElementById('error-message').style.display = 'none';
}

function showError(message) {
    hideLoading();
    
    document.getElementById('error-text').textContent = message;
    document.getElementById('error-message').style.display = 'block';
    document.getElementById('weather-results').style.display = 'none';
}

function showLoading() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('weather-results').style.display = 'none';
    document.getElementById('error-message').style.display = 'none';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}
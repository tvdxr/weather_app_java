document.addEventListener('DOMContentLoaded', function() {
    const locationBtn = document.getElementById('location-btn');
    const locationStatus = document.getElementById('location-status');

    locationBtn.addEventListener('click', function() {
        getUserLocation();
    });

    function getUserLocation() {
        if (!navigator.geolocation) {
            showLocationError('Geolocation is not supported by this browser.');
            return;
        }

        // loading status
        locationBtn.disabled = true;
        locationBtn.textContent = '🔍 Getting location...';
        locationStatus.style.display = 'block';
        locationStatus.textContent = 'Requesting location permission...';
        locationStatus.style.background = '#e8f5e8';
        locationStatus.style.color = '#27ae60';

        const options = {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 300000 // 5 minutes
        };

        navigator.geolocation.getCurrentPosition(
            function(position) {
                const lat = position.coords.latitude;
                const lon = position.coords.longitude;
                
                locationStatus.textContent = `Location found! Getting weather data...`;
                fetchWeatherByLocation(lat, lon);
            },
            function(error) {
                let errorMessage;
                switch(error.code) {
                    case error.PERMISSION_DENIED:
                        errorMessage = "Location access denied. Please enable location permissions and try again.";
                        break;
                    case error.POSITION_UNAVAILABLE:
                        errorMessage = "Location information is unavailable.";
                        break;
                    case error.TIMEOUT:
                        errorMessage = "Location request timed out. Please try again.";
                        break;
                    default:
                        errorMessage = "An unknown error occurred while getting your location.";
                        break;
                }
                showLocationError(errorMessage);
            },
            options
        );
    }

    async function fetchWeatherByLocation(lat, lon) {
        try {
            const response = await fetch(`/api/weather/coordinates?lat=${lat}&lon=${lon}`);
            
            if (response.ok) {
                window.location.href = `weather.html?lat=${lat}&lon=${lon}&location=true`;
            } else {
                const errorText = await response.text();
                showLocationError(`Weather service error: ${errorText}`);
            }
        } catch (error) {
            console.error('Error fetching weather by location:', error);
            showLocationError('Failed to get weather data. Please try again.');
        }
    }

    function showLocationError(message) {
        locationStatus.style.display = 'block';
        locationStatus.style.background = '#ffebee';
        locationStatus.style.color = '#c62828';
        locationStatus.textContent = message;
        
        locationBtn.disabled = false;
        locationBtn.textContent = '📍 Use My Location';
    }
});
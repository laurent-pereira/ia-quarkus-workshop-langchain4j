package dev.langchain4j.quarkus.workshop;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/")
public class WeatherService {
    @RestClient
    WeatherClient wc;

    private static final Logger LOG = Logger.getLogger(WeatherService.class);

    @Path("weather")
    @GET
    public String getWeather(@QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude) {
        LOG.infof("getWeather called with latitude=%f, longitude=%f", latitude, longitude);
        String response = wc.getForecast(
                latitude,
                longitude,
                16,
                "temperature_2m,snowfall,rain,precipitation,precipitation_probability");
        LOG.infof("getWeather response: %s", response);
        return response;
    }
}

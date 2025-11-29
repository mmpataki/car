package car.app.beans;

import car.app.models.reports.StoreConfig;
import car.app.models.reports.Visualization;
import car.util.GsonMaker;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Log4j
public class GsonConfigurer implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter ghmc = new GsonHttpMessageConverter();
        ghmc.setGson(gson());
        converters.add(ghmc);
    }

    @Bean
    public static Gson gson() {
        return GsonMaker.getGson(Visualization.class, StoreConfig.class);
    }

}

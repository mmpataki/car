package car.app.beans;

import car.app.CarAppConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DSBean {

    @Autowired
    CarAppConfiguration conf;

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(conf.getAppDbDriver());
        dataSourceBuilder.url(conf.getAppDbUrl());
        dataSourceBuilder.username(conf.getAppDbUser());
        dataSourceBuilder.password(conf.getAppDbPassword());
        return dataSourceBuilder.build();
    }

}

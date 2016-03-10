package za.co.anylytical.lyticalcns.config;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import org.mongeez.Mongeez;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import za.co.anylytical.lyticalcns.domain.util.JSR310DateConverters;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile(Constants.SPRING_PROFILE_HEROKU)
@EnableMongoRepositories("za.co.anylytical.lyticalcns.repository")
@Import(value = MongoAutoConfiguration.class)
@EnableMongoAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class HerokuDatabaseConfiguration extends AbstractMongoConfiguration {


    private final Logger log = LoggerFactory.getLogger(HerokuDatabaseConfiguration.class);

    public static final String MONGO_URI_BEAN_NAME = "mongoURI";

    @Inject
    private Mongo mongo;


    @SuppressWarnings("deprecation")
    @Bean
    public MongoURI mongoURI() {
        log.info("connecting to db");
        MongoURI mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
        assert mongoURI != null : "missing MONGOHQ_URL";
        return mongoURI;
    }


    @Override
    @DependsOn(MONGO_URI_BEAN_NAME)
    @Bean
    protected String getDatabaseName() {
        return mongoURI().getDatabase();
    }

    @SuppressWarnings("deprecation")
    @Override
    @DependsOn(MONGO_URI_BEAN_NAME)
    @Bean
    public Mongo mongo() throws Exception {
        return new Mongo(mongoURI());
    }


    @Bean
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(JSR310DateConverters.DateToZonedDateTimeConverter.INSTANCE);
        converters.add(JSR310DateConverters.ZonedDateTimeToDateConverter.INSTANCE);
        converters.add(JSR310DateConverters.DateToLocalDateConverter.INSTANCE);
        converters.add(JSR310DateConverters.LocalDateToDateConverter.INSTANCE);
        converters.add(JSR310DateConverters.DateToLocalDateTimeConverter.INSTANCE);
        converters.add(JSR310DateConverters.LocalDateTimeToDateConverter.INSTANCE);
        return new CustomConversions(converters);
    }

    @Bean
    public Mongeez mongeez() {
        log.debug("Configuring Mongeez");
        Mongeez mongeez = new Mongeez();
        mongeez.setFile(new ClassPathResource("/config/mongeez/master.xml"));
        mongeez.setMongo(mongo);
        mongeez.setDbName(getDatabaseName());
        mongeez.process();
        return mongeez;
    }
}

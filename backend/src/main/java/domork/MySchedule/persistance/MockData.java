package domork.MySchedule.persistance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;

@Configuration
public class MockData {


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final DataSource source;

    public MockData(DataSource source) {
        this.source = source;
    }

    @PostConstruct
    void insertDummyData() {
        try (Connection c = source.getConnection()) {
            ScriptUtils.executeSqlScript(c, new ClassPathResource("sql/demoData.sql"));
        } catch (Exception e) {
            LOGGER.error("Error inserting test data", e);
        }
    }
}

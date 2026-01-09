package dtu;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features",
    plugin = { "pretty" }
)
public class RunCucumberTest {
    // Runner for Cucumber tests
}

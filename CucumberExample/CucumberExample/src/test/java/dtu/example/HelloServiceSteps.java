package dtu.example;

import static org.junit.Assert.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelloServiceSteps {
    String result;
    HelloService service = new HelloService();

    @When("I call the hello service")
    public void iCallTheHelloService() {
        result = service.hello();
    }

    @Then("I get the answer {string}")
    public void iGetTheAnswer(String string) {
        assertEquals(string, result);
    }
}

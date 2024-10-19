package com.baratella.piquis.e2e.config;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources",
    glue = "com.baratella.piquis.e2e.steps")
public class CucumberIntegrationTest {

}
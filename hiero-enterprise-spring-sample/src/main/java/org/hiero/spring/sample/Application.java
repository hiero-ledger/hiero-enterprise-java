package org.hiero.spring.sample;

import io.micrometer.core.instrument.MeterRegistry;
import org.hiero.spring.EnableHiero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableHiero
public class Application {

  @Autowired MeterRegistry registry;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}

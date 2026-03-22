package com.smartcampus;

import org.glassfish.jersey.server.ResourceConfig;

public class App extends ResourceConfig {
    public App() {
        packages("com.smartcampus");
    }
}
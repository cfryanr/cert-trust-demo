package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static <T> Class<T> forceClassInitialization(Class<T> klass) {
        try {
            Class.forName(klass.getName(), true, klass.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        return klass;
    }

    public static void main(String[] args) {
        new Thread(() -> {
            // Sleeping 0 seconds will almost always cause the requests to fail.
            // Sleeping even just 2 seconds will make it more likely to succeed.
            int seconds = 2;
            System.out.println("Sleeping " + seconds + " seconds before invoking CloudFoundryCertificateTruster");

            try {
                Thread.sleep(seconds * 1000); // Does it help to wait a little while?
                System.out.println("Done sleeping " + seconds + " seconds before invoking CloudFoundryCertificateTruster");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Invoking CloudFoundryCertificateTruster");
            // We typically don't need to do this, but it seems necessary in cf-for-k8s
            // or else the static initializer block of the class never runs, which is
            // the way that CloudFoundryCertificateTruster is supposed to kick off its work.
            forceClassInitialization(CloudFoundryCertificateTruster.class);
        }).start();

        System.out.println("Starting up the spring app");
        SpringApplication.run(DemoApplication.class, args);
    }

}

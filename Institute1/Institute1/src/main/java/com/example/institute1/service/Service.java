package com.example.institute1.service;

import javafx.concurrent.Task;

public class Service {

    public static class LoadingThread extends Task<Integer> {  // Should extend Task
        @Override
        protected Integer call() throws Exception {
            for (double i = 0; i <= 100; i++) {
                updateProgress(i, 100);  // This updates the progress property
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return 100;
        }
    }
}

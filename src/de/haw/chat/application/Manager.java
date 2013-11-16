package de.haw.chat.application;

import java.util.Observable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.application
 */
public final class Manager extends Observable {

    private static Manager instance;

    private BlockingQueue<Task> queue;

    private Manager() {
        queue = new ArrayBlockingQueue<Task>(50);
    }

    public static Manager getInstance() {
        if(instance == null) {
            instance = new Manager();
        }

        return instance;
    }

    public void publishTask(Task task) {
        try {
            System.out.println(String.format("%s published task %s", Thread.currentThread().getName(), task.getAction()));
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void broadcastTasks() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Task task = queue.take();

                setChanged();
                notifyObservers(task);

                // check for QUIT task
                if(task.getAction().equals(TaskAction.QUIT)) {
                    Thread.currentThread().interrupt();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}

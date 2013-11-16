package de.haw.chat.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.application
 */
public abstract class TaskWorker extends Thread implements Observer {

    protected List<ActionListener> actionListeners;

    protected BlockingQueue<Task> queue;

    /**
     * Template method for initialization hooks
     */
    protected void initialize() { }

    /**
     * Template method for logic to happen before a task is processed
     */
    protected void preProcessTask() { }

    /**
     * Template method for logic to happen after a task is processed
     */
    protected void postProcessTask() { }

    protected void addListener(ActionListener listener) {
        if(actionListeners == null) {
            actionListeners = new ArrayList<ActionListener>();
        }

        actionListeners.add(listener);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Task) {
            Task t = (Task) arg;

            if(queue == null) {
                queue = new ArrayBlockingQueue<Task>(50);
            }

            try {
                queue.put(t);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public synchronized void start() {
        // set name
        setName(getClass().toString());

        // subscribe to main manager
        Manager.getInstance().addObserver(this);

        // start thread
        super.start();
    }

    @Override
    public void run() {
        if(queue == null) {
            queue = new ArrayBlockingQueue<Task>(50);
        }

        // call initialize
        initialize();

        // process incoming tasks
        while(!isInterrupted()) {
            try {
                // take incoming tasks
                Task task = queue.take();

                // call pre process hook
                preProcessTask();

                // process incoming tasks
                for(ActionListener listener : actionListeners) {
                    if(listener.getListenAction().equals(task.getAction())) {
                        listener.process(task);
                    }
                }

                // call post process hook
                postProcessTask();

            } catch (InterruptedException e) {
                // inform us
                System.out.println(String.format("%s stoped and listens no longer to incoming tasks.", this.getClass().toString()));

                // remove from observer
                Manager.getInstance().deleteObserver(this);

                // set interrupt flag back after catching exception
                interrupt();
            }
        }
    }
}

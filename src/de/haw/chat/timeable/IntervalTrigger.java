package de.haw.chat.timeable;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.timeable
 */
public class IntervalTrigger extends Thread {

    private Runnable action;

    private int interval;

    public IntervalTrigger(Runnable action, int interval) {
        this.action = action;
        this.interval = interval;
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            try {
                synchronized (this) {
                    this.wait(interval * 1000);
                }
                action.run();
            } catch (InterruptedException e) {
                interrupt();
                e.printStackTrace();
            }
        }
    }
}

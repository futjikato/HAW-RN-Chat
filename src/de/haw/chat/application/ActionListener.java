package de.haw.chat.application;

import java.util.HashMap;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.application
 */
public abstract class ActionListener {

    protected abstract TaskAction getListenAction();

    protected abstract void onAction(HashMap<String, String> params);

    public void process(Task task) {
        if(task.getAction().equals(getListenAction())) {
            onAction(task.getParameters());
        }
    }
}

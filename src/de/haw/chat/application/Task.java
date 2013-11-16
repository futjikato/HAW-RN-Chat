package de.haw.chat.application;

import java.util.HashMap;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.application
 */
public interface Task {

    public TaskAction getAction();

    public HashMap<String, String> getParameters();
}

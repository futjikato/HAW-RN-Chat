package de.haw.chat.timeable;

import de.haw.chat.application.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.timeable
 */
public class IntervalManager extends TaskWorker {

    private static final int CHECKFORUSER = 5;

    private static IntervalManager instance;

    private List<IntervalTrigger> triggerList;

    private IntervalManager() {
        triggerList = new ArrayList<IntervalTrigger>();

        final Task fetchListTask = new Task() {
            @Override
            public TaskAction getAction() {
                return TaskAction.FETCHUSER_REQUEST;
            }

            @Override
            public HashMap<String, String> getParameters() {
                return null;
            }
        };

        // fetch user list from server task
        final IntervalTrigger fetchListTrigger = new IntervalTrigger(new Runnable() {
            @Override
            public void run() {
                Manager.getInstance().publishTask(fetchListTask);
            }
        }, CHECKFORUSER);

        // if the username is accepted by the server start requesting user list
        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.CUSER_NAMECHANGE;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                if(!IntervalManager.this.triggerList.contains(fetchListTrigger)) {
                    // call once right now
                    Manager.getInstance().publishTask(fetchListTask);
                    // add fetch task to intervalmanager to be called every 5 seconds
                    IntervalManager.this.triggerList.add(fetchListTrigger);
                    fetchListTrigger.start();
                }
            }
        });

        addListener(new ActionListener() {
            @Override
            protected TaskAction getListenAction() {
                return TaskAction.QUIT;
            }

            @Override
            protected void onAction(HashMap<String, String> params) {
                // interrrupt all interval trigger
                for(IntervalTrigger trigger : triggerList) {
                    trigger.interrupt();
                }

                // interrupt self
                interrupt();
            }
        });
    }

    public static IntervalManager getInstance() {
        if(instance == null) {
            instance = new IntervalManager();
        }

        return instance;
    }
}

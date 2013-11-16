package de.haw.chat;

import de.haw.chat.application.Manager;
import de.haw.chat.application.Task;
import de.haw.chat.application.TaskAction;
import de.haw.chat.network.ChatRemoteManager;
import de.haw.chat.peer2peer.P2PManager;
import de.haw.chat.timeable.IntervalManager;
import de.haw.chat.ui.GuiManager;

import java.util.HashMap;

public class Main  {

    public static final int TCP_SERVERPORT = 50000;

    public static void main(String[] args) {
        Manager manager = Manager.getInstance();

        // create manager thread for the tcp connection to the server
        ChatRemoteManager chatRemoteManager = ChatRemoteManager.getInstance();
        chatRemoteManager.start();

        // create manager for gui tasks
        GuiManager guiManager = GuiManager.getInstance();
        guiManager.start();

        // create manager for interval events
        IntervalManager intervalManager = IntervalManager.getInstance();
        intervalManager.start();

        // create p2p manager for udp connections
        P2PManager p2pManager = new P2PManager();
        p2pManager.start();

        manager.broadcastTasks();
    }
}

package de.haw.chat.network;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.network
 */
public enum ClientRequest {
    NEW("NEW"), INFO("INFO"), BYE("BYE");


    private String command;

    ClientRequest(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}

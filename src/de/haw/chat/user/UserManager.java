package de.haw.chat.user;

import de.haw.chat.network.ChatRemoteManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat
 */
public class UserManager {

    protected User system ;

    protected User ownUser;

    protected List<User> remoteUsers;

    protected static UserManager instance;

    private UserManager() {
        // initialize system user
        system = new User("System");

        // initialize ownUser
        ownUser = new User("USER");

        // initialize list for remote users
        remoteUsers = new ArrayList<User>();
    }

    public static UserManager getInstance() {
        if(instance == null) {
            // create singleton
            instance = new UserManager();
        }

        return instance;
    }

    public User addRemoteUser(String username) {
        User newUser = new User(username);
        remoteUsers.add(newUser);

        return newUser;
    }

}

package de.haw.chatserver;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chatserver
 */
public final class UserManager {

    private static UserManager instance;

    private Semaphore semaphore;

    private HashMap<InetAddress, String> userMap;

    public static UserManager getInstance() {
        if(instance == null) {
            instance = new UserManager();
        }

        return instance;
    }

    private UserManager() {
        semaphore = new Semaphore(1);
        userMap = new HashMap<InetAddress, String>();
    }

    public void add(InetAddress address, String name) throws Exception {
        try {
            semaphore.acquire();

            if(userMap.containsKey(address))
                throw new Exception("Only one user per IP allowed.");

            if(userMap.containsValue(name))
                throw new Exception("Username already in use.");

            userMap.put(address, name);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } finally {
            semaphore.release();
        }
    }

    public String getUserListAsString() {
        try {
            semaphore.acquire();

            StringBuilder builder = new StringBuilder(2500);
            builder.append("LIST ");
            builder.append(userMap.size());

            Set<InetAddress> keys = userMap.keySet();
            for(InetAddress key : keys) {
                builder.append(String.format(" %s %s", key.getHostAddress(), userMap.get(key)));
            }

            return builder.toString();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "";
        } finally {
            semaphore.release();
        }
    }

    public void remove(InetAddress inetAddress) {
        try {
            semaphore.acquire();

            if(userMap.containsKey(inetAddress))
                userMap.remove(inetAddress);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }
}

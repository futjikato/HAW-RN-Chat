package de.haw.chat.network;

import java.util.HashMap;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.network
 */
public final class InfoParser {

    private InfoParser() {}

    public static HashMap<String, String> parseInfoResponse(String response) throws Exception {
        if(response.length() < 5)
            throw new Exception("Invalid server response. Must be at least 5 characters length");

        if(response.substring(0, 5).toUpperCase().equals("ERROR"))
            throw new Exception("Error receiving userlist.");

        String[] parts = response.split("\\s");

        if(parts.length < 2)
            throw new Exception("Invalid server response. Unable to parse.");

        String command = parts[0];
        int count = Integer.valueOf(parts[1]);

        HashMap<String, String> userList = new HashMap<String, String>();

        int base = 2;
        for(int i = 0 ; i < count ; i++) {

            if(parts.length < base + 1)
                throw new Exception("Invalid server response. Odd amount of paramater.");

            String host = parts[base];
            String username = parts[base + 1];

            userList.put(host, username);

            base += 2;
        }

        return userList;
    }
}

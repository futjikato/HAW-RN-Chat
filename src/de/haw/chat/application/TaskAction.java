package de.haw.chat.application;

/**
 * @author moritzspindelhirn
 * @todo Documentation
 * @category de.haw.chat.application
 */
public enum TaskAction {

    // System action
    QUIT,
    CONNECT_REQUEST,
    CONNECT_SUCCESS,
    CONNECT_FAILED,
    FETCHUSER_REQUEST,
    FETCHUSER_PROCESS,

    // Chat user triggered actions
    CUSER_REQUESTNAMECHANGE,
    CUSER_REQUESTNAMECHANGEFAILED,
    CUSER_NAMECHANGE,
    CUSER_NEWMESSAGE,

    // Remote chat user triggered actions
    RUSER_ENTERED,
    RUSER_LEFT,
    RUSER_NAMECHANGE,
    RUSER_NEWMESSAGE;

}

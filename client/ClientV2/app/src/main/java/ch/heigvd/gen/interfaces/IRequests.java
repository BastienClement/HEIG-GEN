package ch.heigvd.gen.interfaces;

/**
 * TODO
 */
public interface IRequests {

    String BASE_URL = "http://loki.cpfk.net:9999/api/";
    String LOGIN = "auth";
    String REGISTER = "register";
    String EVENTS = "events";
    String GET_ALL_USERS = "users";
    String GET_ALL_UNKNOWN_USERS = "users/unknowns";
    String GET_USER = "users/";
    String GET_SELF = "users/self";
    String SET_MESSAGES_READ = "/read";

    String GET_CONTACTS = "contacts";
    String GET_CONTACT = "contacts/";

    String GET_MESSAGES = "/messages";

}

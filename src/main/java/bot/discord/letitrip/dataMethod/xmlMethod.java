package bot.discord.letitrip.dataMethod;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bot.discord.letitrip.App;

import java.io.File;

public class xmlMethod {
    static MessageCreateEvent Event;
    static Document Doc;
    public static void initXmlMethod(File file, MessageCreateEvent event) {
        Event=event;
    }
    public static Element addServer() {
        Element server = Doc.createElement("server");
        Element sIdElem = Doc.createElement("serverId");
        Element users = Doc.createElement("users");
        Doc.getChildNodes().item(0).appendChild(server);
        server.appendChild(sIdElem);
        users.appendChild(server);
        return server;
    }

    public static boolean isInitialized(String id) {
        return Doc.getElementById(id) != null;
    }
    public static boolean serverInitialized() {
        boolean initialized = false;
        if(Event.getServer().isPresent()) {
            if (isInitialized(Event.getServer().get().getIdAsString())) {
                initialized = true;
            } else {
                App.notifyServerOwner(Event, "Your server is not set up with the letitrip bot. Would you like to set it up now?" +
                        " Run --setup to enable the bot");
            }
        }
        return initialized;
    }
    public static boolean userInitialized(User user) {
        return isInitialized(user.getIdAsString());
    }

    public static Element addUser(User user) {
        Element server = Doc.getElementById(Event.getServer().get().getIdAsString());
        Element newUser = null;
        if(serverInitialized()) {
            if(userInitialized(user)) {
                System.err.println("User already initialized");
            } else {
                newUser = Doc.createElement("user");
                newUser.setIdAttribute(user.getIdAsString(),true);
                Element users = (Element) server.getChildNodes().item(0);
                users.appendChild(newUser);
            }
        } else {
            return null;
        }
        return newUser;
    }

    public static void addUser(MessageCreateEvent event, Document document) {
        document.getElementById(event.getServer().toString());
        System.out.println(event.getServer().toString());
    }
}

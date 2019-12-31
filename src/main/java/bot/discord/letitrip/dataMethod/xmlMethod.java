package bot.discord.letitrip.dataMethod;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bot.discord.letitrip.App;

public class xmlMethod {
    public static Element addServer(Document doc, MessageCreateEvent event) {
        Element server = doc.createElement("server");
        Element sIdElem = doc.createElement("serverId");
        Element users = doc.createElement("users");
        doc.getChildNodes().item(0).appendChild(server);
        server.appendChild(sIdElem);
        users.appendChild(server);
        return server;
    }

    public static boolean isInitialized(String id, Document document) {
        return document.getElementById(id) != null;
    }
    public static boolean serverInitialized(MessageCreateEvent event, Document document) {
        boolean initialized = false;
        if(event.getServer().isPresent()) {
            if (isInitialized(event.getServer().get().getIdAsString(), document)) {
                initialized = true;
            } else {
                App.notifyServerOwner(event, "Your server is not set up with the letitrip bot. Would you like to set it up now?" +
                        " Run --setup to enable the bot");
            }
        }
        return initialized;
    }
    public static boolean userInitialized(User user, Document document) {
        return isInitialized(user.getIdAsString(), document);
    }

    public static Element addUser(MessageCreateEvent event, User user, Document document) {
        Element server = document.getElementById(event.getServer().get().getIdAsString());
        Element newUser = null;
        if(serverInitialized(event, document)) {
            if(userInitialized(user, document)) {
                System.err.println("User already initialized");
            } else {
                newUser = document.createElement("user");
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

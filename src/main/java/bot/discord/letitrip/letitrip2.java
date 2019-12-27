package bot.discord.letitrip;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.util.logging.ExceptionLogger;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class letitrip2 {

    static final String xmlFilePath = "data.xml"; //we want file path constant
    static DocumentBuilder documentBuilder; //add a global document builder
    static final File xmlFile = new File(xmlFilePath); //we want the file to be constant

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
        if(document.getElementById(id) != null) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean serverInitialized(MessageCreateEvent event, Document document) {
        return isInitialized(event.getServer().get().getIdAsString(), document);
    }
    public static boolean userInitialized(User user, Document document) {
        return isInitialized(user.getIdAsString(), document);
    }

    public static void notifyOwner(MessageCreateEvent event, String message) {
        event.getChannel().sendMessage(event.getServer().get().getOwner().getMentionTag() + " " + message);
    }

    public static Element addUser(MessageCreateEvent event, User user, Document document) {
        Element server = document.getElementById(event.getServer().get().getIdAsString());
        Element newUser = null;
        if(serverInitialized(event, document)) {
            if(userInitialized(user, document)) {
            } else {
                newUser = document.createElement("user");
                newUser.setIdAttribute(user.getIdAsString(),true);
            }
        } else {
            notifyOwner(event, "Your server is not set up with the letitrip bot. Would you like to set it up now?" +
                    " Run --setup to enable the bot");
            return null;
        }
        return newUser;
    }

    public static void main(String[] args) {
        new DiscordApiBuilder().setToken(App.loginToken()//The app.loginToken() is a String method in the class App that returns the token
        ).login().thenAccept(api -> { //login to the bot with the token
            api.addMessageCreateListener(event -> { //add a message listener to the bot
                //the message listener calls whenever a message is sent in a server that runs this bot

                final String callSign = "--"; //a call-sign for the bot

                String message = event.getMessageContent(); //create a string with out of the message content
                TextChannel channel = event.getChannel(); //get the channel that the event was fired in

                if(message.contains(callSign)) {
                    List<User> userList = event.getMessage().getMentionedUsers(); //get a list of users mentioned in the message

                    String sender = event.getMessageAuthor().getName();//get sender name
                    long senderId = event.getMessageAuthor().getId();
                    String serverIdString = event.getServer().toString();

                    Boolean takeAction = false;
                    Boolean CSSync = false;

                    HashMap<Long,Byte> equipped = new HashMap<Long, Byte>();

                    String[] pantEaters = new String[userList.size()];
                    //initialise a string array to the length of users mentioned in the message

                    String[] command = message.split(" ");

                    switch (command[0]) {
                        default:
                            channel.sendMessage("Invalid command. Please check spelling");
                            break;
                        case ("--letitrip") :
                            String opponents = "";

                            for(int i=0; i<userList.size();i++) {
                                pantEaters[i] = userList.get(i).getMentionTag();
                                opponents = opponents.concat(pantEaters[i] + " ");
                            }

                            if(xmlFile.exists()) {
                                Document document = documentBuilder.newDocument();

                                Element server = addServer(document, event);
                                User messageAuthor = event.getMessageAuthor().asUser().get();
                                Element user = addUser(event, messageAuthor, document);

                                Attr serverId = document.createAttribute("id"); //create a serverName attribute
                                server.setIdAttributeNode(serverId, true); //set the server id type to serverId

                                Element users = document.createElement("users");

                                document.getDocumentElement().normalize();
                                //normalize the document to make sure there are no formatting errors

                                Attr userId = document.createAttribute(String.valueOf(event.getMessageAuthor().getId()));
                                user.setAttributeNode(userId);

                                server.appendChild(users);
                                server.appendChild(user);

                            } else {
                            }

                            channel.sendMessage("Will " + opponents + "accept " + sender + "'s challenge?");
                            break;
                    }
                }

                if(message.equalsIgnoreCase("!ping")) {
                    channel.sendMessage("Pong!");
                }

            });

            System.out.println("Invitation URL for the bot: " + api.createBotInvite());

        }).exceptionally(ExceptionLogger.get());
    }
}

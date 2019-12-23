package bot.discord.letitrip;

import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class jcord {

    static final String xmlFilePath = "data.xml"; //we want file path constant
    static DocumentBuilder documentBuilder; //add a global document builder
    static final File xmlFile = new File(xmlFilePath); //we want the file to be constant

    public static void main(String[] args) {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        //initialise the document builder outside try/catch to reduce
        //memory footprint
        try {
            documentBuilder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        assert documentBuilder != null; //make sure the document builder does != null, no-one likes null pointer exceptions
        Document document;

        if(xmlFile.exists()) {
            try {
                document = documentBuilder.parse(xmlFile);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        document = documentBuilder.newDocument(); //create the document

        Element servers = document.createElement("servers"); //create servers element
        document.appendChild(servers); //append to document

        Document finalDocument = document;
        new DiscordApiBuilder().setToken(App.loginToken()//The app.loginToken() is a String method in the class App that returns the token
        ).login().thenAccept(api -> { //login to the bot with the token
            api.addMessageCreateListener(event -> { //add a message listener to the bot
                //the message listener calls whenever a message is sent in a server that runs this bot

                final String callSign = "--"; //a call-sign for the bot

                String message = event.getMessageContent(); //create a string with out of the message content
                TextChannel channel = event.getChannel(); //get the channel that the event was fired in

                if(message.contains(callSign)) {
                    List<User> userList = event.getMessage().getMentionedUsers(); //get a list of users mentioned in

                    String sender = event.getMessageAuthor().getName();//get sender name
                    long senderId = event.getMessageAuthor().getId();
                    String serverIdS = event.getServer().toString();

                    Element server = finalDocument.createElement("server");
                    Attr serverId = finalDocument.createAttribute("id"); //create a serverName attribute
                    server.setIdAttributeNode(serverId, true); //set the server id type to serverId

                    Boolean takeAction = false;
                    Boolean CSSync = false;

                    HashMap<Long,Byte> equipped = new HashMap<Long, Byte>();


                    if(finalDocument.getElementById(serverIdS) != null) {

                        Element node = finalDocument.getElementById(serverIdS);
                        NodeList children = node.getChildNodes();

                        for(int i=0; i<children.getLength(); i++) {

                            Node workingNode = children.item(i);
                            String nodeName = workingNode.getLocalName();

                            if(nodeName.equals("configuration")) {

                                NodeList configNodes = workingNode.getChildNodes();

                                for(byte a=0; a<configNodes.getLength(); a++) {
                                    switch (configNodes.item(a).getNodeName()) {
                                        case("CSSync"):
                                            if(configNodes.item(a).getTextContent().equals("enabled")) {
                                                CSSync = true;
                                            }
                                            break;

                                        case("status"):
                                            if(configNodes.item(a).getTextContent().equals("enabled")) {
                                                takeAction = true;
                                            }
                                            break;
                                    }
                                }
                            } else if(nodeName.equals("users")) {

                                NodeList userNodes = workingNode.getChildNodes();

                                for(byte a=0; a<userNodes.getLength(); a++) {
                                    Element userNode = (Element)userNodes.item(a);
                                    boolean[] found = new boolean[userList.size()];

                                    for(byte b = 0; b<userList.size(); b++) {
                                        if(userNode.getAttribute("id"
                                        ).equals(String.valueOf(userList.get(b).getId()))) {
                                            found[b] = true;
                                            NodeList userData = userNode.getChildNodes();
                                            for(byte c=0; c<userData.getLength(); c++) {
                                                switch (userData.item(c).getNodeName()) {
                                                    case("equipped"):
                                                        Element beyblade = (Element)userData.item(c);
                                                }
                                            }
                                        }
                                    }
                                    for(int b=0; b<found.length; b++) {
                                        if(found[b] == true) {

                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Element config = finalDocument.createElement("configuration");
                        Element serverStatus = finalDocument.createElement("status");
                        config.appendChild(serverStatus);
                    }

                    Element users = finalDocument.createElement("users");
                    Element user = finalDocument.createElement("user");
                    //create elements that are relative to each server

                    finalDocument.getDocumentElement().normalize();
                    //normalize the document to make sure there are no formatting errors

                    String[] pantEaters = new String[userList.size()];
                    //initialise a string array to the length of users mentioned in the message

                    Attr userId = finalDocument.createAttribute(String.valueOf(event.getMessageAuthor().getId()));
                    user.setAttributeNode(userId);

                    server.appendChild(users);
                    server.appendChild(user);

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

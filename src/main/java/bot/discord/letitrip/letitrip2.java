package bot.discord.letitrip;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

    public static void addServer(Document doc, MessageCreateEvent event) {
        Element server = doc.createElement("server");
        Element sIdElem = doc.createElement("serverId");
        Element users = doc.createElement("users");
        doc.getChildNodes().item(0).appendChild(server);
        server.appendChild(sIdElem);
        users.appendChild(server);
    }

    public static void addUser(MessageCreateEvent event, Document document) {
        document.getElementById(event.getServer().toString());
        System.out.println(event.getServer().toString());
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

                    Document document = documentBuilder.newDocument();

                    Element server = document.createElement("server");
                    Attr serverId = document.createAttribute("id"); //create a serverName attribute
                    server.setIdAttributeNode(serverId, true); //set the server id type to serverId

                    Boolean takeAction = false;
                    Boolean CSSync = false;

                    HashMap<Long,Byte> equipped = new HashMap<Long, Byte>();

                    Element users = document.createElement("users");
                    Element user = document.createElement("user");
                    //create elements that are relative to each server

                    document.getDocumentElement().normalize();
                    //normalize the document to make sure there are no formatting errors

                    String[] pantEaters = new String[userList.size()];
                    //initialise a string array to the length of users mentioned in the message

                    Attr userId = document.createAttribute(String.valueOf(event.getMessageAuthor().getId()));
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

                            if(xmlFile.exists()) {
                                SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                                try {
                                    SAXParser parser = parserFactory.newSAXParser();
                                    DefaultHandler handler = new DefaultHandler() {
                                        @Override
                                        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                                            super.startElement(uri, localName, qName, attributes);
                                            System.out.println("URI: " + uri + " LOCALNAME: " + localName + "QNAME: " + qName + "ATTRIBUTES: " + attributes);
                                        }
                                        @Override
                                        public void characters(char[] ch, int start, int length) throws SAXException {
                                            super.characters(ch, start, length);
                                        }

                                        @Override
                                        public void endDocument() throws SAXException {
                                            super.endDocument();
                                        }
                                    };
                                    parser.parse(xmlFile, handler);

                                } catch (ParserConfigurationException e) {
                                    e.printStackTrace();
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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

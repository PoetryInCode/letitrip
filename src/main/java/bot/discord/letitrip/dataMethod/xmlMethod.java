package bot.discord.letitrip.dataMethod;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bot.discord.letitrip.App;
import org.xml.sax.Attributes;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

public class xmlMethod {

    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    SAXParser saxParser;

    static MessageCreateEvent Event;
    static Document Doc;
    static File _file;


    {
        try {
            saxParser = saxParserFactory.newSAXParser();
            class handler extends DefaultHandler {
                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    super.startElement(uri, localName, qName, attributes);
                    if(localName.equals("")) {}
                }
            }
            saxParser.parse(_file, new handler());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void initXmlMethod(File file, MessageCreateEvent event) {
        Event=event;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Doc = builder.parse(file);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            App.sysLog(e.getMessage());
        }
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

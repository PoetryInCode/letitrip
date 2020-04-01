package bot.discord.letitrip.dataMethod;

import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bot.discord.letitrip.App;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;

public class xmlMethod {

    static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance(); //parser factor initialization
    static SAXParser saxParser;

    static {
        try {
            saxParser = saxParserFactory.newSAXParser(); //initialize the parser on startup
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    static MessageCreateEvent Event;
    static Document Doc;
    static File _file;

    public static void initXmlMethod(File file, MessageCreateEvent event) {
        Event=event; //initialize the global event object
        _file=file; //initialize the global _file object
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); //create a Doc-Builder-Fac
        try {
            class handler extends DefaultHandler { //create a Default Handler to handle the xml calls
                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    super.startElement(uri, localName, qName, attributes);
                }
            }
            saxParser.parse(file, new handler()); //parse the xml file with the handler
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Doc = builder.parse(file); //parse the file into the document
        } catch (IOException | ParserConfigurationException | SAXException e) {
            App.sysLog(e.getMessage());
        }
    }

    public static Element addServer(long id) {
        Element servers = (Element)Doc.getChildNodes().item(0);
        Element server = null; //initialize the server element as null
        if(!isInitialized(String.valueOf(id))) { //check if it already exists
            server = Doc.createElement("server"); //create element
            server.setAttribute("id",String.valueOf(id));
            Element users = Doc.createElement("users");
            Doc.getChildNodes().item(0).appendChild(server);
            users.appendChild(server);
        }
        return server;
    }

    public static boolean isInitialized(String id) {
        return Doc.getElementById(id) != null;
    }

    public static boolean serverInitialized() {
        boolean initialized = false;
        if (Event.getServer().isPresent()) {
            if (isInitialized(Event.getServer().get().getIdAsString())) {
                initialized = true;
            } else {
                App.notifyServerOwner(Event,
                        "Your server is not set up with the letitrip bot. Would you like to set it up now?"
                                + " Run --setup to enable the bot");
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

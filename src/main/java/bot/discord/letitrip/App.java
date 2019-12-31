package bot.discord.letitrip;

import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class App {
    static String loginToken() {
        String token = null;
        try {
            Scanner scanner = new Scanner(new File("../token"));
            token = scanner.next();
            System.out.println(token);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return token;
    }
    public static void notifyServerOwner(MessageCreateEvent event, String message) {
        event.getChannel().sendMessage(event.getServer().get().getOwner().getMentionTag() + " " + message);
    }
    public static void sendError(MessageCreateEvent event, String message) {
        event.getChannel().sendMessage(message);
    }
    public static void warnAll(MessageCreateEvent event, String message) {
        notifyServerOwner(event,message);
        sendError(event,message);
    }
}

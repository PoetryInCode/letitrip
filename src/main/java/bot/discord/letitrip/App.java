package bot.discord.letitrip;

import org.javacord.api.event.message.MessageCreateEvent;

import java.io.*;
import java.time.LocalTime;
import java.util.Scanner;

public class App {
    private static final File masterLog = new File("log.txt");
    private static FileOutputStream logStream = null;

    static {
        try {
            logStream = new FileOutputStream(masterLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private static String formatLog(String message) {
        StringBuilder builder = new StringBuilder();
        builder.append(dateTime());
        int returns=0;
        for(int i=0; i<message.length(); i++) {
            if(message.charAt(i) == '\n') {
                returns++;
            }
        }
        if(returns>0) {
            builder.append('\n');
        }
        for(int i=0; i<message.length(); i++) {
            builder.append(message.charAt(i));
            if(message.charAt(i) == '\n') {
                builder.append('\t');
            }
        }
        return builder.toString();
    }

    public static String dateTime() {
        LocalTime time = java.time.LocalTime.now();
        return "[" + java.time.LocalDate.now() + "]~[" + time.getHour() + ":" + time.getMinute() + ":" + time.getSecond() + "]";
    }

    public static void sysLog(String message) {
        System.out.println(formatLog(message));
        if(logStream == null) {
            System.out.println(formatLog(
                    "           ~~~~Critical error~~~~\n" +
                    "     File log stream could not be created\n" +
                    "This should not happen under any circumstances\n" +
                    "           ~~~~~End of Error~~~~~"
            ));
            try {
                throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                logStream.write(formatLog(message).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyServerOwner(MessageCreateEvent event, String message) {
        if(event.getServer().isPresent()) {
            event.getChannel().sendMessage(event.getServer().get().getOwner().getMentionTag() + " " + message);
        }
    }
    private static void sendError(MessageCreateEvent event, String message) {
        event.getChannel().sendMessage(message);
    }
    private static void warnAll(MessageCreateEvent event, String message) {
        notifyServerOwner(event,message);
        sendError(event,message);
    }
}

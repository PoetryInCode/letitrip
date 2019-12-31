package bot.discord.letitrip.dataMethod;

import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class fileMethod {

    static File basePath = new File("./botData.d/");
    static String path;
    static MessageCreateEvent messageEvent;

    public static void createErrorFile() {
        try {
            throw new IOException();
        } catch (IOException e) {
            File errorFile = new File(path + "error.out");
            try {
                FileOutputStream stream = new FileOutputStream(errorFile);
                stream.write(e.getMessage().getBytes());
            } catch (IOException e1) {
                e1.printStackTrace();
                System.err.println(
                        "#######################################################\n" +
                                "#Could not create error file, folder must be read only#\n" +
                                "#######################################################\n"
                );
            }
            e.printStackTrace();
            System.err.println("File already exists or could not create path");
        }
    }

    public static void addServer(MessageCreateEvent event) {
        messageEvent = event;
        String path = basePath.getPath().concat(String.valueOf(event.getServer().get().getId()) + "/");
        File serverFile = new File(path);
        if(serverFile.mkdir()) {
            System.out.println("Created new server directory: \"" + path + "\"");
            path = path.concat("config");
            File serverConfig = new File(path);
            if(serverConfig.canRead()) {
                if(serverConfig.canWrite()) {
                    try {
                        FileOutputStream configStream = new FileOutputStream(serverConfig);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            createErrorFile();
        }
    }
}

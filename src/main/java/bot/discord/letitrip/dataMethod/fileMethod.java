package bot.discord.letitrip.dataMethod;

import bot.discord.letitrip.App;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class fileMethod {

    static File basePath = new File("./botData.d/");
    static File log = new File("log.txt");
    static String path;
    static MessageCreateEvent messageEvent;

    public static void createErrorFile() {
        try {
            throw new IOException();
        } catch (IOException e) {
            File localErrorFile = new File(path + "error.out");
            try {
                FileOutputStream stream = new FileOutputStream(localErrorFile);
                stream.write(e.getMessage().getBytes());
            } catch (IOException e1) {
                App.sysLog(e1.toString());
                App.sysLog(
                        "#######################################################\n" +
                        "#Could not create error file, folder must be read only#\n" +
                        "#######################################################\n"
                );
            }
            e.printStackTrace();
            App.sysLog("File already exists or could not create path");
        }
    }

    public static void addServer(MessageCreateEvent event) {
        messageEvent = event;
        if(event.getServer().isPresent()) {
            String path = basePath.getPath().concat(event.getServer().get().getId() + "/");
            File serverFile = new File(path);
            if(serverFile.mkdir()) {
                App.sysLog("Created new server directory: \"" + path + "\"");
                path = path.concat("config");
                File serverConfig = new File(path);
                if (serverConfig.canRead()) {
                    if (serverConfig.canWrite()) {
                        try {
                            FileOutputStream configStream = new FileOutputStream(serverConfig);
                        } catch (FileNotFoundException e) {
                            App.sysLog(e.toString());
                        }
                    }
                }
            } else {
                createErrorFile();
            }
        }
    }
}

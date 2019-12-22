package bot.discord.letitrip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class App {
    public static String loginToken() throws FileNotFoundException {
        FileInputStream stream = new FileInputStream(new File("../token.txt"));
        String token = stream.toString();
        return token;
    }
}

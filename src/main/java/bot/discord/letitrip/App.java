package bot.discord.letitrip;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class App {
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
}

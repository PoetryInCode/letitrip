package bot.discord.letitrip.dataMethod.dataExceptions;

public class ServerFileException extends Exception {
    class ServerFileExistsException {
        private String message = "oof";
        public ServerFileExistsException(String message) {
            super();
        }
    }
}

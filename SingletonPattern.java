/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Pattern;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author poornimaepy
 */
public class SingletonPattern {

    // Static inner class implementing the Singleton pattern
    private static class Logger {
        private static Logger logger;

        private Logger() {}

        public static Logger getInstance() {
            if (logger == null) {
                synchronized (Logger.class) {
                    if (logger == null) {
                        logger = new Logger();
                    }
                }
            }
            return logger;
        }

        public void log(String message) {
            System.out.println(message);
        }
    }

    public static void main(String[] args) {
        Logger logger = Logger.getInstance();
        logger.log("This is a singleton logger example.");
    }
}

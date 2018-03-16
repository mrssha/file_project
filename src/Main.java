import java.util.Scanner;

/**
 * Created by Maria on 16.03.2018.
 */
public class Main {

    public static void main(String[] args){
        GDriveProvider gdrive = new GDriveProvider();
        ConsoleCommands commands = new ConsoleCommands(gdrive);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to file_project");

        commands.connect();

        System.out.println("help - to get help");
        System.out.println("quit - to quit");
        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.next();
            if (command.equals("quit")) {
                return;
            } else if (command.equals("help")) {
                commands.help();
            } else if (command.equals("list")) {
                commands.list();
            } else if (command.equals("upload")) {
                commands.upload();
            } else {
                System.out.println("Unknown command");
            }
        }
    }

}

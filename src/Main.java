import java.util.Scanner;

/**
 * Created by Maria on 16.03.2018.
 */
public class Main {

    public static void main(String[] args){
        GDriveProvider gdrive = new GDriveProvider();
        ConsoleCommands commands = new ConsoleCommands(gdrive);
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");

        System.out.println("Welcome to file_project");

        try {
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
                } else if (command.startsWith("upload ")) {
                    commands.upload(command.substring(7));
                } else if (command.startsWith("download ")) {
                    commands.download(command.substring(9));
                } else {
                    System.out.println("Unknown command");
                }
            }
        }
        catch (Exception e) {
            System.out.println("Unhandled exception");
        }
    }

}

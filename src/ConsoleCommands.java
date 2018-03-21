import java.util.Scanner;

public class ConsoleCommands {

    private GDriveProvider _gdrive;

    public ConsoleCommands(GDriveProvider gdrive) {
        _gdrive = gdrive;
    }

    public void help() {

    }

    public void connect() {
        System.out.println("Authentication needed");
        System.out.println("Go the following url in the browser:");
        System.out.println(_gdrive.GetAuthorizationUrl());
        System.out.print("\nAuthorization code: ");

        Scanner scanner = new Scanner(System.in);
        String code = scanner.next();
        GDriveProvider.AuthToken token = _gdrive.GetAuthToken(code);
    }

    public void list() {
        _gdrive.getFileList();
    }

    public void upload() {

    }
}

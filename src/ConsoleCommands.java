import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConsoleCommands {

    private GDriveProvider _gdrive;
    private String _propertiesFileName = "privite_properties.txt";

    public ConsoleCommands(GDriveProvider gdrive) {
        _gdrive = gdrive;
    }

    public void help() {

    }



    public void connect() throws java.io.FileNotFoundException {
        String authProperties = _readProperties();
        if (!authProperties.isEmpty())
        {
            System.out.println("Authentication properties loaded");
            _gdrive.setupTokens(authProperties);
        }
        else {
            System.out.println("Authentication needed");
            System.out.println("Go the following url in the browser:");
            System.out.println(_gdrive.GetAuthorizationUrl());
            System.out.print("\nAuthorization code: ");

            // get code and fetch tokens
            Scanner scanner = new Scanner(System.in);
            String code = scanner.next();
            authProperties = _gdrive.GetAuthProperties(code, "");
            _writeProperites(authProperties);
            _gdrive.setupTokens(authProperties);
        }

        System.out.println("Name: " + _gdrive.getProfileName());

    }

    public void list() {
        System.out.println("File list: " + _gdrive.getFileList());
    }

    public void upload() {
        //_gdrive.UploadFile(Paths.get(""));

    }

    private String _readProperties() {
        try {
            return new String(Files.readAllBytes(Paths.get(_propertiesFileName)));
        }
        catch (java.io.IOException e) {
            return "";
        }
    }

    private void _writeProperites(String authProperties) throws java.io.FileNotFoundException {
        PrintWriter out = new PrintWriter(_propertiesFileName);
        out.print(authProperties);
        out.close();
    }
}

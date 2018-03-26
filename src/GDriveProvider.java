import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GDriveProvider {

    String _clientId = "359097715554-9l17d9b86g2h84cvtk23aarjtae690ud.apps.googleusercontent.com";
    String _clientSecret = "EihzgWNJKZSZGmV-9znM72Fu";
    String _accessToken = "";
    String _refreshToken = "";

    public String GetAuthorizationUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "scope=https://www.googleapis.com/auth/drive%20profile&" +
                "response_type=code&" +
                "redirect_uri=urn:ietf:wg:oauth:2.0:oob&" +
                "client_id=" + _clientId;
    }

    public String GetAuthProperties(String code, String refreshToken) {
        try {
            String strUrl = "https://www.googleapis.com/oauth2/v4/token";
            String requestParams = "client_id=" + _clientId + "&" +
                    "client_secret=" + _clientSecret;
            if (refreshToken.isEmpty()) {
                requestParams += "&code=" + code + "&" +
                    "redirect_uri=urn:ietf:wg:oauth:2.0:oob&" +
                        "grant_type=authorization_code";
            } else {
                requestParams += "&refresh_token=" + refreshToken + "&" +
                        "grant_type=refresh_token";
            }

            byte[] data = (requestParams).getBytes("UTF-8");
            return _fetchPostResponse(strUrl, data, "application/x-www-form-urlencoded");
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setupTokens(String authProperties) {
        Matcher accessTokenMatcher = Pattern.compile("access_token\": \"(.*?)\"").matcher(authProperties);
        Matcher refreshTokenMatcher = Pattern.compile("refresh_token\": \"(.*?)\"").matcher(authProperties);
        if (accessTokenMatcher.find())
            _accessToken = accessTokenMatcher.group(1);
        if (refreshTokenMatcher.find())
            _refreshToken = refreshTokenMatcher.group(1);
    }

    public void connect() {

    }

    public void close() {

    }

    public String getProfileName() {
        String response = _toString(_fetchResponse("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=youraccess_token"));
        JSONObject obj = new JSONObject(response);
        return obj.getString("name");
    }

    public String getFileList() {
        String response = _toString(_fetchResponse("https://content.googleapis.com/drive/v3/files"));
        JSONObject obj = new JSONObject(response);
        JSONArray files_info = obj.getJSONArray("files");

        String result = "";
        for (int i = 0; i < files_info.length(); i++) {
            if (!result.isEmpty()) result += "\n";
            result += files_info.getJSONObject(i).getString("name") + ", id: " + files_info.getJSONObject(i).getString("id");
        }
        return result;
    }

    public void uploadFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] fileContent =  Files.readAllBytes(path);
            String response = _fetchPostResponse("https://www.googleapis.com/upload/drive/v3", fileContent, "image/jpeg");
        }
        catch (Exception e){
        }

    }

    public void downloadFile(String fileId) {
        try {
            //  + "?alt=media"
            String url = "https://www.googleapis.com/drive/v3/files/" + fileId;
            String response = _toString(_fetchResponse(url));
            String fileName = new JSONObject(response).getString("name");
            byte[] fileContent = _toBytes(_fetchResponse(url + "?alt=media"));

            String userHomeFolder = System.getProperty("user.home");
            FileOutputStream stream = new FileOutputStream(userHomeFolder + "/Desktop/" + fileName);
            stream.write(fileContent);
            stream.close();
        }
        catch (Exception e){
        }

    }

    private InputStream  _fetchResponse(String strUrl) {
        return _fetchResponse(strUrl, false);
    }

    private InputStream  _fetchResponse(String strUrl, boolean secondTime) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Properties in order to ensure succesful POST-request
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Specify request-method and headers
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");

            // Specify your credentials
            connection.setRequestProperty("Authorization", "Bearer " + _accessToken);

            // Read response
            int responseCode = connection.getResponseCode();
            return connection.getInputStream();
        }
        catch (Exception e) {
            if (secondTime)
                return null;
            _refreshTokens();
            return _fetchResponse(strUrl, true);
        }
    }

    private String _toString(InputStream is)
    {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    private byte[] _toBytes(InputStream is)
    {
        try {
            int nRead;
            byte[] data = new byte[16384];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private String _fetchPostResponse(String strUrl, byte[] data, String contentType)
    {
        try{
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Properties in order to ensure succesful POST-request
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Specify request-method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
            connection.setRequestProperty("Content-Type", contentType);

            OutputStream out = connection.getOutputStream();
            out.write(data);
            out.flush();

            // Read response
            int responseCode = connection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        catch (Exception e) {
            return null;
        }
    }

    private void _refreshTokens() {
        String authProperties = GetAuthProperties("", _refreshToken);
        setupTokens(authProperties);
    }
}

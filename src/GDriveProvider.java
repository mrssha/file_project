import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.*;

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
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Properties in order to ensure succesful POST-request
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Specify request-method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("accept", "application/json");

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
        String response = _fetchResponse("https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=youraccess_token");
        JSONObject obj = new JSONObject(response);
        return obj.getString("name");
    }

    public List<String> getFileList() {
        _fetchResponse("https://content.googleapis.com/drive/v3/files");
        List<String> list =  new ArrayList<String>();
        return list;
    }

    public void UploadFile(String filePath) {

    }

    private String _fetchResponse(String strUrl) {
        return _fetchResponse(strUrl, false);
    }

    private String _fetchResponse(String strUrl, boolean secondTime) {
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
            if (secondTime)
                return "";
            _refreshTokens();
            return _fetchResponse(strUrl, true);
        }
    }

    private void _refreshTokens() {
        String authProperties = GetAuthProperties("", _refreshToken);
        setupTokens(authProperties);
    }
}

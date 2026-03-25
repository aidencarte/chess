package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Collection;
import java.util.Map;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        var request = buildRequest("POST", "/user", registerRequest);
        var response = sendRequest(request, null);
        return handleResponse(response, RegisterResult.class);
    }

    public void clearDb(int id) throws ResponseException {
        var request = buildRequest("DELETE", "/db", null);
        var response = sendRequest(request, null);
    }


    public GameData[] listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null);
        var response = sendRequest(request, authToken);
        var handledResponse = handleResponse(response, GameData[].class);
        return handledResponse != null ? handledResponse : new GameData[0];
    }

    public LoginResult loginUser(LoginRequest loginRequest) throws ResponseException{
        var request = buildRequest("POST", "/session", loginRequest);
        var response = sendRequest(request, null);
        return handleResponse(response, LoginResult.class);
    }

    public void logoutUser(String authToken) throws ResponseException
    {
        var request = buildRequest("DELETE", "/session", authToken);
        sendRequest(request, authToken);
    }

    public GameData createGame(String gameName, AuthData authData) throws ResponseException
    {
        var request = buildRequest("POST", "/game", gameName);
        var response = sendRequest(request, authData.authToken());
        return handleResponse(response, GameData.class);
    }

    public GameData joinGame(String authToken, int gameID, ChessGame.TeamColor teamColor) throws ResponseException
    {
        var joinGameReq = new JoinGameRequest(teamColor, gameID);
        var request = buildRequest("PUT", "/game", joinGameReq);
        var response = sendRequest(request, authToken);
        return handleResponse(response, GameData.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request, String authToken) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
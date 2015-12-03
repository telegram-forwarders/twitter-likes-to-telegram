package com.github.soramusoka.twitter_likes_to_telegram_bot;

import org.apache.log4j.Logger;

public class TelegramBot {
    private String _token;
    private String _chatId;
    private HttpRequest _request;
    private String _hostname = "api.telegram.org";
    private Logger _logger;

    public TelegramBot(String token, String chatId, HttpRequest request, Logger logger) {
        this._token = token;
        this._chatId = chatId;
        this._request = request;
        this._logger = logger;
    }

    public void sendMessage(String message) {
        this.sendMessage(this._token, this._chatId, message);
    }

    public void sendMessage(String token, String chatId, String message) {
        try {
            String url = "https://" + this._hostname + "/bot" + token + "/sendMessage?chat_id=" + chatId + "&text=" + message;
            this._logger.debug("broadcast message: " + message);
            this._request.getUrl(url);
        } catch (Exception e) {
            String msg = "send message error: " + e.toString();
            this._logger.error(msg);
        }
    }
}

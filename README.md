# twitter-likes-to-telegram-bot

### Requrements
- java8
- telegram bot (register here)

### Usage

```
java -jar out/artifacts/twitter_likes_to_telegram_bot_jar/twitter-likes-to-telegram-bot.jar --consumerKey=[consumerKey] --consumerSecret=[consumerSecret] --accessToken=[accessToken] --accessSecret=[accessSecret] --teleToken=[teleToken] --teleChat=[teleChat] --user=[username]
```

#### params:
	--consumerKey=[consumerKey] - Required, Twitter consumer key, string
	--consumerSecret=[consumerSecret] - Required, Twitter consumer secret, string
	--accessToken=[accessToken] - Required, Twitter access token, string
	--accessSecret=[accessSecret] - Required, Twitter access secret, string
	--teleToken=[teleToken] - Required, Telegram acess token, string
	--teleChat=[teleChat] - Required, Telegram chat id, positive number
	--user=[username] - Required, Twitter username, string. Could be use multiples times to ass many users

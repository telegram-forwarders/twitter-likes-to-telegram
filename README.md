# Twitter Likes forwarder to Telegram chat

### Requrements
- java8
- telegram bot (register here)

### Usage

```bash
usage: java -cp twitter_telegram_forwarder.jar twitter.telegram.forwarder.Main
       [-a <arg>] [-e <arg>] [-h] [-k <arg>] [-o <arg>] [-r <arg>] [-s <arg>]
       [-t <arg>] [-u <arg>]
Options
   -a,--telegramChat <arg>              Telegram chat id. Positive number.
                                        Required
   -e,--twitterConsumerSecret <arg>     Twitter consumer secret. Required
   -h,--help                            Show usage
   -k,--twitterConsumerKey <arg>        Twitter consumer key. Required
   -o,--telegramToken <arg>             Telegram access token. Required
   -r,--requestCounterMax <arg>         Twitter request try counter per user.
                                        Positive number. Optional
   -s,--twitterAccessSecret <arg>       Twitter access secret. Required
   -t,--twitterAccessToken <arg>        Twitter access token. Required
   -u,--user <arg>                      Twitter user. Could be used to add
                                        multiple values. Required
```
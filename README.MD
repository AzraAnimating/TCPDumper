# TCP Dumper

A Monitoring Application which logs all TCP Traffic using TCPDump when a certain threshold
is passed.




Timezones can be aquired here:

http://worldtimeapi.org/api/timezone/

### Requires:

- Java 11 or Higher
- tcpdump
- nload


### Configuration

```JSON
{
  "unitToTrigger": "mbit", Determinesat what unit a Dump should be triggered at
  "bandWidth": 50.0,
  "cooldownToNextDumpMS": 10000, Cooldown to next dump in Milliseconds
  "tcpDumpDuration": "10m", Duration of the TCPDump
  "networkInterface": "eth0", Interface that should be monitored
  "discordWebhooks": "insertWebhook", Discord Webhooks, devided by ","
  "telegramWebhooks": "insertWebhook",
  "ip": "1.1.1.1",
  "serverName": "TestServer",
  "location": "OVH",
  "timeZone": "europe/berlin"
}
```
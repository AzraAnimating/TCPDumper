# TCP Dumper

A Monitoring Application which logs all TCP Traffic using TCPDump when a certain threshold
is passed.

This is used to dump TCP Based DDoS attacks when one is detected.

### Requires:

- Java 11 or Higher
- tcpdump
- nload


### Configuration

```JSON
{
  "unitToTrigger": "mbit",
  "bandWidth": 50.0,
  "cooldownToNextDumpMS": 10000,
  "tcpDumpDuration": "10m",
  "networkInterface": "eth0",
  "discordWebhooks": "insertWebhook",
  "telegramWebhooks": "insertWebhook",
  "ip": "1.1.1.1",
  "serverName": "TestServer",
  "location": "OVH",
  "maxPacketsPerDump": 0,
  "additionalParameters": "-v -S"
}
```

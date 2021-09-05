package de.azraanimating.tcpdumper;

import okhttp3.*;

import java.io.IOException;

public class WebhookManager {

    private final TCPDumper tcpDumper;
    private final OkHttpClient okHttpClient;


    public WebhookManager(TCPDumper tcpDumper) {
        this.tcpDumper = tcpDumper;
        this.okHttpClient = new OkHttpClient().newBuilder().build();
    }

    public void sendDiscordNotifications(String poT, String magnitude) throws IOException {
        if(!this.tcpDumper.getConfig().discordWebhooks.equals("insertWebhook")) {
            String[] webhooks = this.tcpDumper.getConfig().discordWebhooks.split(",");
            if(webhooks.length > 1) {
                for (int i = 0; i < webhooks.length; i++) {
                    String webhook = webhooks[i];
                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create("" +
                            "{\n" +
                            "  \"content\": null,\n" +
                            "  \"embeds\": [\n" +
                            "    {\n" +
                            "      \"title\": \"DDoS Attack detected on " + this.tcpDumper.getConfig().serverName + "\",\n" +
                            "      \"description\": \"" + this.tcpDumper.getConfig().location + "\",\n" +
                            "      \"color\": 16711680,\n" +
                            "      \"fields\": [\n" +
                            "        {\n" +
                            "          \"name\": \"Attack Details\",\n" +
                            "          \"value\": \"Target: ``" + this.tcpDumper.getConfig().ip + "``\\nBandwidth: ``" + magnitude + "/s``\\nTCPDump Duration: ``" + this.tcpDumper.getConfig().tcpDumpDuration + "``\\nTime: ``" + poT + "``\"\n" +
                            "        }\n" +
                            "      ],\n" +
                            "      \"author\": {\n" +
                            "        \"name\": \"TCP Dump Notify\",\n" +
                            "        \"url\": \"https://github.com/AzraAnimating/TCPDumper\"\n" +
                            "      }\n" +
                            "    }\n" +
                            "  ]\n" +
                            "}", mediaType);
                    Request request = new Request.Builder()
                            .url(webhook)
                            .method("POST", body)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    this.okHttpClient.newCall(request).execute();
                }
            } else {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create("" +
                        "{\n" +
                        "  \"content\": null,\n" +
                        "  \"embeds\": [\n" +
                        "    {\n" +
                        "      \"title\": \"DDoS Attack detected on " + this.tcpDumper.getConfig().serverName + "\",\n" +
                        "      \"description\": \"" + this.tcpDumper.getConfig().location + "\",\n" +
                        "      \"color\": 16711680,\n" +
                        "      \"fields\": [\n" +
                        "        {\n" +
                        "          \"name\": \"Attack Details\",\n" +
                        "          \"value\": \"Target: ``" + this.tcpDumper.getConfig().ip + "``\\nBandwidth: ``" + magnitude + "/s``\\nTCPDump Duration: ``" + this.tcpDumper.getConfig().tcpDumpDuration + "``\\nTime: ``" + poT + "``\"\n" +
                        "        }\n" +
                        "      ],\n" +
                        "      \"author\": {\n" +
                        "        \"name\": \"TCP Dump Notify\",\n" +
                        "        \"url\": \"https://github.com/AzraAnimating/TCPDumper\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}", mediaType);
                Request request = new Request.Builder()
                        .url(this.tcpDumper.getConfig().discordWebhooks)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                this.okHttpClient.newCall(request).execute();
            }
        }
    }

    public void sendTelegramNotification(String poT, String magnitude) throws IOException {
        if(!this.tcpDumper.getConfig().telegramWebhooks.equals("insertWebhook")) {
            String[] webhooks = this.tcpDumper.getConfig().discordWebhooks.split(",");
            if(webhooks.length > 1) {
                for (int i = 0; i < webhooks.length; i++) {
                    String webhook = webhooks[i];
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url(webhook + magnitude + "/s Surge detected on " + this.tcpDumper.getConfig().serverName + " " + this.tcpDumper.getConfig().ip + " at " + this.tcpDumper.getConfig().location + " at " + poT)
                            .method("GET", null)
                            .build();
                    client.newCall(request).execute().close();
                }
            } else {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url(this.tcpDumper.getConfig().telegramWebhooks + magnitude + "/s Surge detected on " + this.tcpDumper.getConfig().serverName + " " + this.tcpDumper.getConfig().ip + " at " + this.tcpDumper.getConfig().location + " at " + poT)
                        .method("GET", null)
                        .build();
                client.newCall(request).execute().close();
            }
        }
    }

}

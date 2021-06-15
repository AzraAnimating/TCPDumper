package de.azraanimating.tcpdumper;

import de.azraanimating.tcpdumper.config.Config;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TCPDumper {


    private Config config;
    private int triggerScale;
    private long lastTrigger;
    private DateTimeFormatter dateTimeFormatter;
    private WebhookManager webhookManager;


    public TCPDumper() {
        this.lastTrigger = 0;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");
        this.startProcess();
    }

    private void startProcess() {
        this.printStartupGraphic();
        try {
            this.config = Config.fromFile(new File("config.json"));
            this.webhookManager = new WebhookManager(this);
            this.triggerScale = this.checkUnitScale(this.config.unitToTrigger);
            Thread.sleep(1000);
            System.out.println("Startup Completed");
            String[] args = new String[]{"/bin/bash", "-c", "nload devices " + this.config.networkInterface, "with", "args"};
            Process process = new ProcessBuilder(args).start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            try {
                this.print(process, reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void print(Process process, BufferedReader console) throws IOException {
        String line;
        while ((line = console.readLine()) != null) {
            if(!process.isAlive()) {
                process.destroy();
            }
            if(line.contains("Curr:")) {
                String[] parts = line.split("Curr: ");
                String[] partsParts = parts[1].split("/s");
                System.out.println(partsParts[0] + "/s");
                this.filter(partsParts[0]);
            };
        }
    }

    private void printDump(Process process, BufferedReader console) throws IOException {
        String line;
        while ((line = console.readLine()) != null) {
            if(!process.isAlive()) {
                process.destroy();
            }
            //System.out.println(line);
        }
    }

    private void filter(String string) throws IOException {
        String[] bandwidth = string.split(" ");
        String unit = bandwidth[1];
        double amount = Double.parseDouble(bandwidth[0]);

        if(amount >= this.config.bandWidth) {
            if(this.checkUnitScale(unit) >= this.triggerScale && (System.currentTimeMillis() - this.lastTrigger) > this.config.cooldownToNextDumpMS) {
                this.lastTrigger = System.currentTimeMillis();
                String poT = this.getDateTime();
                System.out.println("TRIGGERED AT " + poT + " RUNNING FOR " + this.config.tcpDumpDuration);
                this.triggerTCPDump(poT, amount + " " + unit);
            }
        }
    }

    private int checkUnitScale(String unit) {
        switch (unit.toLowerCase(Locale.ROOT)) {
            case "bit":
                return 0;
            case "kbit":
                return 1;
            case "mbit":
                return 2;
            case "gbit":
                return 3;
        }
        return 0;
    }

    private void triggerTCPDump(String poT, String magnitude) throws IOException {
        this.webhookManager.sendDiscordNotifications(poT, magnitude);
        this.webhookManager.sendTelegramNotification(poT, magnitude);
        ThreadHandler.startExecute(() -> {
            String[] args = new String[]{"/bin/bash", "-c", "timeout " + this.config.tcpDumpDuration + " tcpdump -n -l | tee " + poT + ".out", "with", "args"};
            try {
                Process process = new ProcessBuilder(args).start();
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                printDump(process, reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Config getConfig() {
        return config;
    }

    private void printStartupGraphic() {
        System.out.println("" +
                "@@@@@@@   @@@@@@@  @@@@@@@      @@@@@@@   @@@  @@@  @@@@@@@@@@   @@@@@@@   @@@@@@@@  @@@@@@@ \n" +
                "@@@@@@@  @@@@@@@@  @@@@@@@@     @@@@@@@@  @@@  @@@  @@@@@@@@@@@  @@@@@@@@  @@@@@@@@  @@@@@@@@\n" +
                "  @@!    !@@       @@!  @@@     @@!  @@@  @@!  @@@  @@! @@! @@!  @@!  @@@  @@!       @@!  @@@\n" +
                "  !@!    !@!       !@!  @!@     !@!  @!@  !@!  @!@  !@! !@! !@!  !@!  @!@  !@!       !@!  @!@\n" +
                "  @!!    !@!       @!@@!@!      @!@  !@!  @!@  !@!  @!! !!@ @!@  @!@@!@!   @!!!:!    @!@!!@! \n" +
                "  !!!    !!!       !!@!!!       !@!  !!!  !@!  !!!  !@!   ! !@!  !!@!!!    !!!!!:    !!@!@!  \n" +
                "  !!:    :!!       !!:          !!:  !!!  !!:  !!!  !!:     !!:  !!:       !!:       !!: :!! \n" +
                "  :!:    :!:       :!:          :!:  !:!  :!:  !:!  :!:     :!:  :!:       :!:       :!:  !:!\n" +
                "   ::     ::: :::   ::           :::: ::  ::::: ::  :::     ::    ::        :: ::::  ::   :::\n" +
                "   :      :: :: :   :           :: :  :    : :  :    :      :     :        : :: ::    :   : :\n" +
                "                                                                                             \n" +
                "TCP Dumper by AzraAnimating");
    }

    private String getDateTime() throws IOException {
        /*OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("http://worldtimeapi.org/api/timezone/" + this.config.timeZone)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()) {
            if(response.body() != null) {
                String data = Objects.requireNonNull(response.body()).string();
                JSONObject jsonObject = new JSONObject(data);
                response.close();

                return jsonObject.getString("datetime");
            }
        }
         */
        LocalDateTime localDateTime = LocalDateTime.now();
        return this.dateTimeFormatter.format(localDateTime);
    }
}

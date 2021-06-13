package de.azraanimating.tcpdumper;

import de.azraanimating.tcpdumper.config.Config;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TCPDumper {


    private Config config;
    private int triggerScale;
    private long lastTrigger;
    private DateTimeFormatter dateTimeFormatter;


    public TCPDumper() {
        this.lastTrigger = 0;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");
        try {
            this.config = Config.fromFile(new File("config.json"));
            this.triggerScale = this.checkUnitScale(this.config.unitToTrigger);
            //Thread.sleep(1000);
            String[] args = new String[]{"/bin/bash", "-c", "nload", "with", "args"};
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
                this.filter(partsParts[0]);
            }
        }
    }

    private void filter(String string) throws IOException {
        String[] bandwidth = string.split(" ");
        String unit = bandwidth[1];
        double amount = Double.parseDouble(bandwidth[0]);

        if(amount >= this.config.bandWidth) {
            if(this.checkUnitScale(unit) >= this.triggerScale && (System.currentTimeMillis() - this.lastTrigger) > this.config.cooldownToNextDumpMS) {
                this.lastTrigger = System.currentTimeMillis();
                LocalDateTime now = LocalDateTime.now();
                String poT = this.dateTimeFormatter.format(now);
                System.out.println("TRIGGERED AT " + poT);
                this.triggerTCPDump(poT);
            }
        }

        System.out.println(amount + " " + unit);
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

    private void triggerTCPDump(String poT) throws IOException {
        String[] args = new String[]{"/bin/bash", "-c", "timeout " + this.config.tcpDumpDuration + " tcpdump -n -l | tee " + poT + ".out", "with", "args"};
        Process process = new ProcessBuilder(args).start();
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

}

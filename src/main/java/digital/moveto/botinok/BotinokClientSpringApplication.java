package digital.moveto.botinok;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotinokClientSpringApplication {

    public static void main(String[] args) {
        System.out.println("Hello, Botinok!");
        String x = "Tel Aviv-Yafo Tel Aviv District";

        System.out.println(x.substring(x.lastIndexOf(",") +1).trim());
    }}

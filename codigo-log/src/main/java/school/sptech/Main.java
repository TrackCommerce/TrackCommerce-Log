package school.sptech;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        // Específicando o formato da saída
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        // Importando a classe LocalDateTime
        String dataHora = LocalDateTime.now().format(formato);

        try {
            while (true) {
                Integer porcentagemCpu = ThreadLocalRandom.current().nextInt(50, 101);
                Integer porcentagemRam = ThreadLocalRandom.current().nextInt(50, 101);
                Integer porcentagemDisco = ThreadLocalRandom.current().nextInt(50, 101);

                Integer erroBucket = ThreadLocalRandom.current().nextInt(0, 6);

                if (erroBucket.equals(1)) {
                    System.out.println(dataHora + " [ERROR] bucket error - Incorrect bucket query or a deeper-seated error");
                    break;

                } else {
                    if (porcentagemCpu >= 80) {
                        System.out.println(dataHora + " [WARN] excessive CPU usage - The CPU is using approx " + porcentagemCpu + "%");
                    } else {
                        System.out.println(dataHora + " [INFO] bucket record - A CPU log was created with " + porcentagemCpu + "%");
                    }


                    Thread.sleep(3000);


                    if (porcentagemRam >= 80) {
                        System.out.println(dataHora + " [WARN] excessive RAM usage - The RAM is using approx " + porcentagemRam + "%");
                    } else {
                        System.out.println(dataHora + " [INFO] bucket record - A RAM log was created with " + porcentagemRam + "%");
                    }

                    Thread.sleep(3000);

                    if (porcentagemDisco >= 80) {
                        System.out.println(dataHora + " [WARN] excessive Storage usage - The Storage is using approx " + porcentagemDisco + "%");
                    } else {
                        System.out.println(dataHora + " [INFO] bucket record - A Storage log was created with " + porcentagemDisco + "%");
                    }

                    Thread.sleep(5000);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
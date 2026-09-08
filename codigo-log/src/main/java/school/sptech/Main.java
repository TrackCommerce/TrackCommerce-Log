package school.sptech;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Integer parametroCpu = 80;
        Integer parametroMemoriaRam = 80;
        Integer parametroDisco = 80;

        try {
            System.out.println(LocalDateTime.now().format(formato) + " [INFO] initiated the capture - The user initiated the capture of data from the server.");
            Thread.sleep(2000);

            while (true) {
                Integer porcentagemCpu = ThreadLocalRandom.current().nextInt(40, 101);
                Integer porcentagemRam = ThreadLocalRandom.current().nextInt(40, 101);
                Integer porcentagemDisco = ThreadLocalRandom.current().nextInt(40, 101);

                Integer erroBucket = ThreadLocalRandom.current().nextInt(0, 6);

                if (erroBucket.equals(1)) {
                    Integer continuarError = ThreadLocalRandom.current().nextInt(1, 5);

                    for(Integer i = 0; i < continuarError; i++){
                        System.out.println(LocalDateTime.now().format(formato) + " [ERROR] bucket error - Incorrect bucket insertion or a deeper-seated error");
                        Thread.sleep(2000);
                    }

                } else {
                    if (porcentagemCpu >= parametroCpu) {
                        System.out.println(LocalDateTime.now().format(formato) + " [WARN] excessive CPU usage - The CPU is using approx " + porcentagemCpu + "%");
                    } else {
                        System.out.println(LocalDateTime.now().format(formato) + " [INFO] bucket record - A CPU log was created with " + porcentagemCpu + "%");
                    }


                    Thread.sleep(2000);


                    if (porcentagemRam >= parametroMemoriaRam) {
                        System.out.println(LocalDateTime.now().format(formato) + " [WARN] excessive RAM usage - The RAM is using approx " + porcentagemRam + "%");
                    } else {
                        System.out.println(LocalDateTime.now().format(formato) + " [INFO] bucket record - A RAM log was created with " + porcentagemRam + "%");
                    }

                    Thread.sleep(2000);

                    if (porcentagemDisco >= parametroDisco) {
                        System.out.println(LocalDateTime.now().format(formato) + " [WARN] excessive Storage usage - The Storage is using approx " + porcentagemDisco + "%");
                    } else {
                        System.out.println(LocalDateTime.now().format(formato) + " [INFO] bucket record - A Storage log was created with " + porcentagemDisco + "%");
                    }

                    Thread.sleep(2000);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
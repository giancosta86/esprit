package info.gianlucacosta.esprit.util;

import java.time.Duration;
import java.time.Instant;

public interface Timing {
    static void measure(Runnable action) {
        Instant start =
                Instant.now();

        try {
            action.run();
        } finally {
            Instant stop =
                    Instant.now();

            Duration duration =
                    Duration.between(start, stop);

            int minutes =
                    (int) duration.getSeconds() / 60;

            int seconds =
                    (int) duration.getSeconds() % 60;

            System.out.println(
                    String.format(
                            "Elapsed time: %s minutes, %s seconds",
                            minutes,
                            seconds
                    )
            );
        }
    }
}

package info.gianlucacosta.esprit.util;

import java.util.concurrent.atomic.AtomicLong;

public class ItemsTracker {
    private static final String batchOutput = "*";
    private static final long viewportCharWidth = 80;

    private final AtomicLong itemsCounter =
            new AtomicLong();

    private final long batchSize;


    public ItemsTracker(long batchSize) {
        this.batchSize = batchSize;
    }


    public Long count() {
        return itemsCounter.get();
    }


    public void trackItem() {
        long itemsCount =
                itemsCounter.incrementAndGet();

        if (itemsCount % (batchSize * viewportCharWidth) == 0) {
            System.out.println(batchOutput);
        } else if (itemsCount % batchSize == 0) {
            System.out.print(batchOutput);
        }
    }
}

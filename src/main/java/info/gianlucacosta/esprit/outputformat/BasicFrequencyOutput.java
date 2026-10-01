package info.gianlucacosta.esprit.outputformat;

public class BasicFrequencyOutput {
    private final String item;
    private final long frequency;


    public BasicFrequencyOutput(Object item, long frequency) {
        this.item =
                item.toString();

        this.frequency =
                frequency;
    }
}

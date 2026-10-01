package info.gianlucacosta.esprit.outputformat;

import info.gianlucacosta.rayon.morphology.Ending;
import info.gianlucacosta.rayon.morphology.Gender;
import info.gianlucacosta.rayon.statistics.FrequencyTracker;

public class GenderFrequencyOutput {
    private final String ending;
    private final String mainGender;
    private final double masculinePercentage;
    private final double epicenePercentage;
    private final double femininePercentage;
    private final long totalCount;


    public GenderFrequencyOutput(Ending ending, FrequencyTracker<Gender> frequencyTracker) {
        this.ending =
                ending.getExpression();

        this.mainGender =
                frequencyTracker.getMainClass().get().getGraceCode();

        this.masculinePercentage =
                frequencyTracker.getPercentage(
                        Gender.MASCULINE
                );

        this.epicenePercentage =
                frequencyTracker.getPercentage(
                        Gender.EPICENE
                );

        this.femininePercentage =
                frequencyTracker.getPercentage(
                        Gender.FEMININE
                );

        this.totalCount =
                frequencyTracker.getTotalCount();
    }
}

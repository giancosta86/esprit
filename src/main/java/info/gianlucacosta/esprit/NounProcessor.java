package info.gianlucacosta.esprit;

import info.gianlucacosta.esprit.outputformat.GenderFrequencyOutput;
import info.gianlucacosta.rayon.morphology.Ending;
import info.gianlucacosta.rayon.morphology.Gender;
import info.gianlucacosta.rayon.morphology.Noun;
import info.gianlucacosta.rayon.morphology.transform.EndingTransform;
import info.gianlucacosta.rayon.statistics.FrequencyTracker;
import info.gianlucacosta.rayon.statistics.FrequencyTrackerIndex;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class NounProcessor {
    private final FrequencyTrackerIndex<Ending, Gender> genderByEndingFrequencyTrackerIndex =
            new FrequencyTrackerIndex<>();

    private final FrequencyTracker<EndingTransform> singularToPluralTransforms =
            new FrequencyTracker<>();


    public void processNoun(Noun noun) {
        analyzeGenderByEnding(noun);
        analyzeSingularAndPlural(noun);
    }


    private void analyzeGenderByEnding(Noun noun) {
        noun.getGender().ifPresent(gender -> {
            String nounExpression =
                    noun
                            .getSingular()
                            .orElseGet(() ->
                                    noun.getPlural().get()
                            );

            Ending
                    .getEndings(nounExpression, 5)
                    .forEach(
                            ending ->
                                    genderByEndingFrequencyTrackerIndex.trackOccurrence(ending, gender)
                    );
        });
    }


    private void analyzeSingularAndPlural(Noun noun) {
        noun.getSingular().ifPresent(singular -> {
            noun.getPlural().ifPresent(plural -> {
                EndingTransform transform =
                        EndingTransform.compute(singular, plural);

                singularToPluralTransforms.trackOccurrence(transform);
            });
        });
    }


    public void outputResults(Path rootOutputDirPath) {
        ProcessorOutputProtocol.printProcessorHeader("NOUNS");

        Path nounsTargetDirPath =
                rootOutputDirPath.resolve("nouns");

        outputGenderByEnding(nounsTargetDirPath);

        outputSingularToPluralTransforms(nounsTargetDirPath);
    }


    private void outputGenderByEnding(Path outputDirPath) {
        ProcessorOutputProtocol.saveActivityResult(
                "Gender by ending",
                outputDirPath,
                "genderByEnding.json",
                () -> {
                    genderByEndingFrequencyTrackerIndex
                            .filterTrackers(50);

                    genderByEndingFrequencyTrackerIndex
                            .simplify(3.5);


                    return StreamSupport.stream(
                            genderByEndingFrequencyTrackerIndex.spliterator(),
                            false
                    )
                            .map(indexPair -> {
                                Ending ending =
                                        indexPair.getKey();

                                FrequencyTracker<Gender> frequencyTracker =
                                        indexPair.getValue();

                                return new GenderFrequencyOutput(
                                        ending,
                                        frequencyTracker
                                );
                            })
                            .collect(Collectors.toList());
                }
        );
    }


    private void outputSingularToPluralTransforms(Path outputDirPath) {
        ProcessorOutputProtocol.saveFrequencyActivityResult(
                "Singular -> Plural transforms",
                outputDirPath,
                "singularToPlural.json",
                singularToPluralTransforms,
                15
        );
    }
}

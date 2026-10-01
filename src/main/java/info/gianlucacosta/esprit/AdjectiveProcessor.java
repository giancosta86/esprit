package info.gianlucacosta.esprit;

import info.gianlucacosta.rayon.morphology.Adjective;
import info.gianlucacosta.rayon.morphology.Ending;
import info.gianlucacosta.rayon.morphology.Gender;
import info.gianlucacosta.rayon.morphology.Number;
import info.gianlucacosta.rayon.morphology.transform.EndingTransform;
import info.gianlucacosta.rayon.statistics.FrequencyTracker;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

class AdjectiveProcessor {
    private static final EndingTransform alToAlsTransform =
            new EndingTransform("", "s");


    private final FrequencyTracker<EndingTransform> masculineToFeminineTransforms =
            new FrequencyTracker<>();

    private final FrequencyTracker<EndingTransform> singularToPluralTransforms =
            new FrequencyTracker<>();

    private final FrequencyTracker<Ending> singularEndings =
            new FrequencyTracker<>();

    private final Set<String> alToAlsAdjectives =
            new HashSet<>();


    public void processAdjective(Adjective adjective) {
        analyzeMasculineToFeminineTransforms(adjective);
        analyzeSingularToPluralTransforms(adjective);
        analyzeSingularEndings(adjective);
    }


    private void analyzeMasculineToFeminineTransforms(Adjective adjective) {
        adjective.getMasculine().ifPresent(masculine -> {
            adjective.getFeminine().ifPresent(feminine -> {
                EndingTransform masculineToFeminineTransform =
                        EndingTransform.compute(masculine, feminine);

                masculineToFeminineTransforms.trackOccurrence(masculineToFeminineTransform);
            });
        });
    }


    private void analyzeSingularToPluralTransforms(Adjective adjective) {
        adjective.getMasculine().ifPresent(singular -> {
            adjective.getInflection(Gender.MASCULINE, Number.PLURAL).ifPresent(plural -> {
                EndingTransform singularToPluralTransform =
                        EndingTransform.compute(singular, plural);

                singularToPluralTransforms.trackOccurrence(singularToPluralTransform);

                if (singular.endsWith("al") && singularToPluralTransform.equals(alToAlsTransform)) {
                    alToAlsAdjectives.add(singular);
                }
            });
        });
    }


    private void analyzeSingularEndings(Adjective adjective) {
        adjective.getMasculine().ifPresent(singular -> {
            Ending
                    .getEndings(singular,
                            2,
                            5
                    )
                    .forEach(singularEndings::trackOccurrence);
        });
    }


    public void outputResults(Path rootOutputDirPath) {
        ProcessorOutputProtocol.printProcessorHeader("ADJECTIVES");

        Path adjectivesTargetDirPath =
                rootOutputDirPath.resolve("adjectives");


        outputMasculineToFeminineTransforms(adjectivesTargetDirPath);
        outputSingularToPluralTransforms(adjectivesTargetDirPath);
        outputSingularEndings(adjectivesTargetDirPath);
        outputAlToAlsAdjectives(adjectivesTargetDirPath);

        System.out.println();
    }


    private void outputMasculineToFeminineTransforms(Path outputDirPath) {
        ProcessorOutputProtocol.saveFrequencyActivityResult(
                "Masculine -> Feminine transforms",
                outputDirPath,
                "masculineToFeminine.json",
                masculineToFeminineTransforms,
                10
        );
    }


    private void outputSingularToPluralTransforms(Path outputDirPath) {
        ProcessorOutputProtocol.saveFrequencyActivityResult(
                "Singular -> Plural transforms",
                outputDirPath,
                "singularToPlural.json",
                singularToPluralTransforms,
                8
        );
    }


    private void outputSingularEndings(Path outputDirPath) {
        ProcessorOutputProtocol.saveFrequencyActivityResult(
                "Singular endings",
                outputDirPath,
                "singularEndings.json",
                singularEndings,
                50
        );
    }


    private void outputAlToAlsAdjectives(Path outputDirPath) {
        ProcessorOutputProtocol.saveActivityResult(
                "-al -> -als adjectives",
                outputDirPath,
                "alToAlsPlural.json",
                () ->
                        alToAlsAdjectives
                                .stream()
                                .sorted()
                                .collect(Collectors.toList())
        );
    }
}

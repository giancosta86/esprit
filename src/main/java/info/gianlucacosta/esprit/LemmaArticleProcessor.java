package info.gianlucacosta.esprit;

import info.gianlucacosta.rayon.morphology.LemmaArticle;

import java.nio.file.Path;
import java.util.regex.Pattern;

class LemmaArticleProcessor {
    private static final Pattern validArticleTitlePattern =
            Pattern.compile("(?i)[a-z][a-z-]+[a-z]");


    private static final NounProcessor nounProcessor =
            new NounProcessor();

    private static final AdjectiveProcessor adjectiveProcessor =
            new AdjectiveProcessor();


    public void process(LemmaArticle article) {
        if (article.getTitle().matches(validArticleTitlePattern.pattern())) {
            article
                    .getNouns()
                    .forEach(nounProcessor::processNoun);

            article
                    .getAdjectives()
                    .forEach(adjectiveProcessor::processAdjective);
        }
    }


    public void outputResults(Path rootOutputDirPath) {
        nounProcessor.outputResults(rootOutputDirPath);
        adjectiveProcessor.outputResults(rootOutputDirPath);
    }
}

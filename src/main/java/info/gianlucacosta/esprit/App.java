package info.gianlucacosta.esprit;

import info.gianlucacosta.esprit.util.Formatters;
import info.gianlucacosta.esprit.util.ItemsTracker;
import info.gianlucacosta.esprit.util.Timing;
import info.gianlucacosta.rayon.morphology.GlawiParser;
import info.gianlucacosta.rayon.util.ExecutorUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    private static final ExecutorService processingService =
            Executors.newCachedThreadPool();

    private static final LemmaArticleProcessor lemmaArticleProcessor =
            new LemmaArticleProcessor();

    private static final ItemsTracker processedArticlesTracker =
            new ItemsTracker(2500);


    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Params: <source GLAWI .xml file> <output directory>");
            System.exit(1);
        }


        Path sourcePath =
                Paths.get(args[0]);


        Path rootOutputDirPath =
                Paths.get(args[1]);


        Timing.measure(() -> {
            parseGlawi(sourcePath);
        });


        Timing.measure(() -> {
            outputResults(rootOutputDirPath);
        });
    }


    private static void parseGlawi(Path sourcePath) {
        try (GlawiParser parser = new GlawiParser(
                Files.newBufferedReader(sourcePath)
        )) {
            parser.parse(lemmaArticle -> {
                processingService.submit(() -> {
                    lemmaArticleProcessor.process(lemmaArticle);

                    processedArticlesTracker.trackItem();
                });
            });

            ExecutorUtils.stopAndJoin(processingService);

            System.out.println();

            System.out.println(
                    String.format(
                            "%s articles processed",
                            Formatters.longFormatter.format(
                                    processedArticlesTracker.count()
                            )
                    )
            );

            System.out.println();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private static void outputResults(Path rootOutputDirPath) {
        try {
            lemmaArticleProcessor.outputResults(rootOutputDirPath);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

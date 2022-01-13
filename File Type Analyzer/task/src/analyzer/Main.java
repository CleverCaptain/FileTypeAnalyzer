package analyzer;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {
    public static void main(String[] args) {
        String folderName = args[0];
        String dbName = args[1];
        List<Pattern> patterns = new ArrayList<>();


        try (Scanner fileScanner = new Scanner(new File(dbName))) {
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                String[] data = line.split(";");
//                System.out.println(Arrays.toString(data));
                patterns.add(new Pattern(Integer.parseInt(data[0]),
                        data[1].substring(1, data[1].length() - 1),
                        data[2].substring(1, data[2].length() - 1)));
            }


            ExecutorService executorService = Executors.newCachedThreadPool();
            File[] files = new File(folderName).listFiles();
//            System.out.println("file count = " + files.length);
            List<FileAnalyzer> fileAnalyzers = new ArrayList<>();
            for (File file : files) {
                FileAnalyzer fileAnalyzer = new FileAnalyzer();
                fileAnalyzer.setFilePatterns(patterns);
                fileAnalyzer.setFileToAnalyze(file);
                fileAnalyzers.add(fileAnalyzer);

//                System.out.println("added file = " + file.getAbsolutePath());

            }
            for (FileAnalyzer fileAnalyzer : fileAnalyzers) {
                executorService.submit(fileAnalyzer);

            }
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.MINUTES);
//            System.out.println(executorService.isTerminated());
        } catch (InterruptedException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

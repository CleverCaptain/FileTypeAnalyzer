package analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileAnalyzer implements Runnable {

    private List<Pattern> filePatterns;
    private String fileName;
    private File fileToAnalyze;
    private String fileBinaryData;
    private String fileType;

    public FileAnalyzer() {
        filePatterns = new ArrayList<>();
        fileType = "[UNKNOWN]";
    }

    public FileAnalyzer(String fileName) {
        filePatterns = new ArrayList<>();
        this.fileName = fileName;
        fileToAnalyze = new File(fileName);
        try (FileInputStream fileInputStream = new FileInputStream(fileToAnalyze);) {
            fileBinaryData = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileType = "[UNKNOWN]";
    }

    public void addFileType(int priority, String pattern, String fileName) {
        filePatterns.add(new Pattern(priority, pattern, fileName));
    }

    public void analyzeFileNaive() {
        String text = fileBinaryData;

        Stopwatch.reset();
        int wordLength = text.length();
        boolean foundFileType = false;
        for (Pattern filePattern : filePatterns) {
            String pattern = filePattern.getPattern();
            int patternLength = pattern.length();
            if (wordLength < patternLength) {
                System.out.println(0);
                return;
            } else if (pattern.isEmpty()) {
                System.out.println(1);
                System.out.println(0);
                return;
            }

            for (int i = 0; i < wordLength - patternLength + 1; i++) {
                boolean foundPattern = true;
                for (int j = 0; j < patternLength; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        foundPattern = false;
                        break;
                    }
                }
                if (foundPattern) {
                    fileType = filePattern.getResultFile();
                    System.out.println(fileType);
                    foundFileType = true;
                    stopAndPrintTime();
                    break;
                }
            }
            if (foundFileType) {
                break;
            }
        }
        if (!foundFileType) {
            System.out.println("Unknown file type");
            stopAndPrintTime();
        }
    }

    public String analyzeFileKMP() {

        Stopwatch.reset();

        for (Pattern filePattern : filePatterns) {

            String pattern = filePattern.getPattern();
//            System.out.println("Pattern to look = " + pattern);
            int[] prefixFunc = prefixFunction(pattern);
            int j = 0;
            for (int i = 0; i < fileBinaryData.length(); i++) {
                while (j > 0 && fileBinaryData.charAt(i) != pattern.charAt(j)) {
                    j = prefixFunc[j - 1];
                }
                if (fileBinaryData.charAt(i) == pattern.charAt(j)) {
                    j++;
                }
                if (j == pattern.length()) {
                    fileType = filePattern.getResultFile();
                    System.out.println(fileName + ": " + fileType);
                    return fileType;
                }
            }
        }
        System.out.println(fileName + ": Unknown file type");
        return "Unknown file type";

    }

    public void analyzeRabinKarp() {
//        Stopwatch.reset();

        for (Pattern filePattern : filePatterns) {
            String pattern = filePattern.getPattern();

            int d = 53;
            long q = 1_000_000_000 + 9;

            if (fileBinaryData.length() < pattern.length()) {
                return;
            }

            int M = pattern.length();
            int N = fileBinaryData.length();
            int i, j;
            long p = 0; // hash value for pattern
            long t = 0; // hash value for txt
            long h = 1;

            // The value of h would be "pow(d, M-1)%q"
            for (i = 0; i < M - 1; i++)
                h = (h * d) % q;

            // Calculate the hash value of pattern and first
            // window of text
            for (i = 0; i < M; i++) {
                p = (d * p + pattern.charAt(i)) % q;
                t = (d * t + fileBinaryData.charAt(i)) % q;
            }

            // Slide the pattern over text one by one
            for (i = 0; i <= N - M; i++) {

                // Check the hash values of current window of text
                // and pattern. If the hash values match then only
                // check for characters on by one
                if (p == t) {
                    /* Check for characters one by one */
                    for (j = 0; j < M; j++) {
                        if (fileBinaryData.charAt(i + j) != pattern.charAt(j))
                            break;
                    }

                    // if p == t and pat[0...M-1] = txt[i, i+1, ...i+M-1]
                    if (j == M) {
                        fileType = filePattern.getResultFile();
                        System.out.println(fileName + ": " + fileType);
                        return;
                    }
                }

                // Calculate hash value for next window of text: Remove
                // leading digit, add trailing digit
                if (i < N - M) {
                    t = (d * (t - fileBinaryData.charAt(i) * h) + fileBinaryData.charAt(i + M)) % q;

                    // We might get negative value of t, converting it
                    // to positive
                    if (t < 0)
                        t = (t + q);
                }
            }

        }

        System.out.println(fileName + ": Unknown file type");

    }

    private static long charToLong(char ch) {
        return ch - 'A' + 1;
    }


    private static int[] prefixFunction(String str) {
        int[] prefixFunc = new int[str.length()];
        for (int i = 1; i < prefixFunc.length; i++) {
            int j = prefixFunc[i - 1];

            while (j > 0 && str.charAt(i) != str.charAt(j)) {
                j = prefixFunc[j - 1];
            }

            if (str.charAt(i) == str.charAt(j)) {
                j++;
            }

            prefixFunc[i] = j;
        }
        return prefixFunc;
    }

    private void stopAndPrintTime() {
        double elapsedSeconds = Stopwatch.getElapsedSeconds();
        System.out.printf("It took %.3f seconds", elapsedSeconds);
    }

    public void setFileToAnalyze(File fileToAnalyze) {
        this.fileToAnalyze = fileToAnalyze;
        fileName = fileToAnalyze.getName();
        try (FileInputStream fileInputStream = new FileInputStream(fileToAnalyze);) {
            fileBinaryData = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileType = "[UNKNOWN]";
    }

    public void setFilePatterns(List<Pattern> filePatterns) {
        filePatterns.sort(Comparator.comparing(Pattern::getPriority).reversed());
        this.filePatterns = filePatterns;
    }

    @Override
    public void run() {
        analyzeFileKMP();
//        analyzeRabinKarp();
    }
}

package analyzer;

public class Pattern {
    private int priority;
    private String pattern;
    private String resultFile;

    public Pattern(int priority, String pattern, String resultFile) {
        this.priority = priority;
        this.pattern = pattern;
        this.resultFile = resultFile;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "priority=" + priority +
                ", pattern='" + pattern + '\'' +
                ", resultFile='" + resultFile + '\'' +
                '}';
    }
}

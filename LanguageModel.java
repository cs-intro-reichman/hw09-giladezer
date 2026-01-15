import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    HashMap<String, List> CharDataMap;
    int windowLength;
    private Random randomGenerator;

    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<>();
    }

    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<>();
    }

    public void calculateProbabilities(List list) {
        int total = 0;
        for (int i = 0; i < list.getSize(); i++)
            total += list.get(i).count;

        double cp = 0.0;
        for (int i = 0; i < list.getSize(); i++) {
            CharData cd = list.get(i);
            cd.p = (double) cd.count / total;
            cp += cd.p;
            cd.cp = cp;
        }
    }

    public char getRandomChar(List list) {
        double r = randomGenerator.nextDouble();
        for (int i = 0; i < list.getSize(); i++) {
            if (list.get(i).cp > r)
                return list.get(i).chr;
        }
        return list.get(list.getSize() - 1).chr;
    }

    public void train(String fileName) {
        In in = new In(fileName);
        String window = "";

        for (int i = 0; i < windowLength; i++)
            window += in.readChar();

        while (!in.isEmpty()) {
            char c = in.readChar();
            List probs = CharDataMap.get(window);
            if (probs == null) {
                probs = new List();
                CharDataMap.put(window, probs);
            }
            probs.update(c);
            window = window.substring(1) + c;
        }

        for (List probs : CharDataMap.values())
            calculateProbabilities(probs);
    }

    public String generate(String initialText, int textLength) {
        if (initialText.length() < windowLength)
            return initialText;

        String result = initialText;

        while (result.length() < textLength) {
            String window = result.substring(result.length() - windowLength);
            List probs = CharDataMap.get(window);
            if (probs == null)
                break;
            result += getRandomChar(probs);
        }

        return result;
    }
}

import java.util.HashMap;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
        String text;

        try {
            text = Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            return;
        }

        if (text.length() < windowLength) {
            return;
        }

        // חלון ראשון
        String window = text.substring(0, windowLength);

        // מעבר תו-תו
        for (int i = windowLength; i < text.length(); i++) {
            char nextChar = text.charAt(i);

            List probs = CharDataMap.get(window);

            if (probs == null) {
                probs = new List();
                probs.addFirst(nextChar);
                CharDataMap.put(window, probs);
            } else {
                probs.update(nextChar);
            }

            // הזזת החלון
            window = window.substring(1) + nextChar;
        }

        // חישוב הסתברויות לכל חלון
        for (List probs : CharDataMap.values()) {
            calculateProbabilities(probs);
        }
    }


        // Computes and sets the probabilities (p and cp fields) of all the
        // characters in the given list. */
        void calculateProbabilities(List probs) {
        if (probs.getSize() == 0) {
            return;
        }

        int total = 0;
        ListIterator iter = probs.listIterator(0);

        // חישוב מספר ההופעות הכולל
        while (iter.hasNext()) {
            CharData cd = iter.next();
            total += cd.count;
        }

        // חישוב p ו-cp
        double cumulative = 0.0;
        iter = probs.listIterator(0);

        while (iter.hasNext()) {
            CharData cd = iter.next();
            cd.p = (double) cd.count / total;
            cumulative += cd.p;
            cd.cp = cumulative;
        }
    }


    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
        double rand = randomGenerator.nextDouble();
        ListIterator iter = probs.listIterator(0);
        while (iter.hasNext()) {
            CharData cd = iter.next();
            if (rand <= cd.cp) {
                return cd.chr;
            }
        }
        // Fallback, should not reach here if probabilities are set correctly
        return probs.getFirst().chr;
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {

    // If the initial text is shorter than the window length,
    // we cannot generate any text
    if (initialText.length() < windowLength) {
        return initialText;
    }

    // Use StringBuilder to efficiently build the generated text
    StringBuilder generated = new StringBuilder(initialText);

    // Generate characters until reaching the desired text length
    while (generated.length() < textLength) {

        int len = generated.length();

        // Extract the last windowLength characters as the current window
        String window = generated.substring(len - windowLength, len);

        // Get the probability list associated with this window
        List probs = CharDataMap.get(window);

        // If the window does not exist in the model, stop generating
        if (probs == null) {
            break;
        }

        // Draw a random character according to the learned probabilities
        char nextChar = getRandomChar(probs);

        // Append the generated character to the text
        generated.append(nextChar);
    }

    // Return the generated text
    return generated.toString();
}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}

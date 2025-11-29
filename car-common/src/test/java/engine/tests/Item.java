package engine.tests;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model for parsing toString() output.
 * <p>
 * Caveat: if values/strings contain '[' or ']' one might get unexpected
 * results.
 *
 * @author dschreiber
 */
public abstract class Item {

    private static final Logger LOGGER = Logger.getLogger(Item.class);

    public static class ValueItem extends Item {
        public ValueItem(String stringRepresentation) {
            super(stringRepresentation);
        }

        public boolean isNullOrEmpty() {
            return Strings.isNullOrEmpty(getStringRepresentation())
                    || "null".equals(getStringRepresentation());
        }
    }

    public static class ObjectItem extends Item {

        private final String type;

        private final Map<String, Item> attributes = new HashMap<String, Item>();

        public ObjectItem(String stringRepresentation) {
            super(stringRepresentation);
            Pattern typePattern = Pattern.compile("(^[A-Z]\\S*) \\[(.*)\\]$",
                    Pattern.DOTALL);
            Matcher typeMatcher = typePattern.matcher(stringRepresentation);
            if (typeMatcher.matches()) {
                type = typeMatcher.group(1);
                for (String attributeValue : splitOnFirstLevelCommaRespectEqualSign(typeMatcher
                        .group(2))) {
                    Iterator<String> split = Splitter.on("=").trimResults()
                            .limit(2).split(attributeValue).iterator();
                    String attributeName = split.next();
                    String attributeValueString = split.next();
                    attributes.put(attributeName,
                            parseString(attributeValueString));
                }
            } else {
                throw new IllegalArgumentException(
                        "cannot create object from string: "
                                + stringRepresentation);
            }
        }

        public String getType() {
            return type;
        }

        public Map<String, Item> getAttributes() {
            return attributes;
        }

        @Override
        public String toString() {
            return super.toString()
                    + "\n Type="
                    + type
                    + "\n  "
                    + Joiner.on("\n  ").withKeyValueSeparator(" = ")
                    .join(attributes);
        }
    }

    public static class ListItem extends Item {

        private List<Item> values = new ArrayList<Item>();

        public ListItem(String stringRepresentation) {
            super(stringRepresentation);
            // remove "[" and "]":
            String valueString = stringRepresentation.substring(1,
                    stringRepresentation.length() - 1);
            LOGGER.debug("no brackets - list: " + valueString);
            for (String value : splitOnFirstLevelComma(valueString)) {
                values.add(parseString(value));
            }

        }

        public List<Item> getValues() {
            return values;
        }

        @Override
        public String toString() {
            return super.toString() + "\n  " + Joiner.on("\n  ").join(values);
        }
    }

    private final String stringRepresentation;

    public Item(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
        LOGGER.info("creating: " + stringRepresentation);
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

    @Override
    public String toString() {
        return "Item [stringRepresentation=" + stringRepresentation + "]";
    }

    /**
     * counts occurence of {@code count} in {@code string}
     *
     * @param string
     * @param count
     * @return
     */
    private static int contains(String string, char count) {
        int counter = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == count) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * only the first comma before an equal sign ('=') is used for split. (So
     * that strings that contain a comma are not split.)
     *
     * @param string
     * @return
     */
    public static List<String> splitOnFirstLevelCommaRespectEqualSign(
            String string) {
        List<String> allSplits = splitOnFirstLevelComma(string);
        List<String> result = new ArrayList<String>(allSplits.size());
        for (String current : allSplits) {
            if (current.contains("=")) {
                result.add(current);
            } else {
                if (result.isEmpty()) {
                    throw new IllegalStateException(
                            "first comma must not occur before first equal sign! ("
                                    + string + ")");
                }
                result.set(result.size() - 1, result.get(result.size() - 1)
                        + ", " + current);
            }
        }
        return result;
    }

    /**
     * ignores commas nested in square brackets ("[", "]")
     *
     * @param string
     */
    public static List<String> splitOnFirstLevelComma(String string) {
        Scanner scanner = new Scanner(string);
        scanner.useDelimiter(", ");
        List<String> result = new ArrayList<String>();
        int openBrackets = 0;
        while (scanner.hasNext()) {
            String next = scanner.next();
            int open = contains(next, '[');
            int close = contains(next, ']');

            LOGGER.debug("openBrackets: " + openBrackets + ", open: " + open
                    + ", close: " + close + ", next: " + next);

            if (openBrackets > 0) {
                result.set(result.size() - 1, result.get(result.size() - 1)
                        + ", " + next);
            } else {
                result.add(next);
            }
            openBrackets = openBrackets + open - close;
        }
        scanner.close();

        return result;
    }

    public static Item parseString(String string) {
        if (Strings.isNullOrEmpty(string)
                || Strings.isNullOrEmpty(string.trim())) {
            return new ValueItem(string);
        }
        Pattern objectPattern = Pattern.compile("^[A-Z][^ ]* \\[.*",
                Pattern.DOTALL);
        string = string.trim();
        if (string.startsWith("[")) {
            return new ListItem(string);
        } else if (objectPattern.matcher(string).matches()) {
            return new ObjectItem(string);
        } else {
            return new ValueItem(string);
        }
    }
}

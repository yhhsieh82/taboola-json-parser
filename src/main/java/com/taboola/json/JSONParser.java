package com.taboola.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JSONParser support parsing the json value into a java object.
 * A JSON value is either: object, array, string, number, true, false or null.
 * <p>
 * These JSON value types can be broadly categorized as:
 * - Simple Values: string, number, true, false or null.
 * - Composite Values: object, array (as they are composed of other JSON values).
 */
public class JSONParser {
    
    public static Object parse(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        String trimmed = jsonString.trim();
        return parseValue(trimmed, new int[]{0});
    }

    /**
     * Parse a JSON value
     * A JSON value is either: object, array, string, number, true, false, null
     * @param json the string containing a json value.
     * @param inIndexHolder array of one int which stores the starting index and will be updated to the position after the value
     * @return the parsed object
     */
    public static Object parseValue(String json, int[] inIndexHolder) {
        int i = inIndexHolder[0];
        char c = json.charAt(i);
        int[] indexHolder = new int[1];

        // Skip whitespace
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }

        Object value;
        if (c == '{') {
            // locate the object first
            int braceCount = 1;
            int objStart = i;
            i++;
            while (i < json.length() && braceCount > 0) {
                if (json.charAt(i) == '{') braceCount++;
                else if (json.charAt(i) == '}') braceCount--;
                i++;
            }

            if (braceCount > 0) {
                throw new IllegalArgumentException("Invalid JSON Object.");
            }

            // parse the located object
            indexHolder[0] = objStart;
            value = parseObject(json, i, indexHolder);
            inIndexHolder[0] = indexHolder[0];
        } else if (c == '[') {
            // Parse array
            int braceCount = 1;
            int objStart = i;
            i++;
            while (i < json.length() && braceCount > 0) {
                if (json.charAt(i) == '[') braceCount++;
                else if (json.charAt(i) == ']') braceCount--;
                i++;
            }

            if (braceCount > 0) {
                throw new IllegalArgumentException("Invalid JSON Array.");
            }

            // parse the located array
            indexHolder[0] = objStart;
            value = parseArray(json, i, indexHolder);
            inIndexHolder[0] = indexHolder[0];
        } else if (c == '"') {
            // Parse string
            indexHolder[0] = i;
            value = parseString(json, indexHolder);
            inIndexHolder[0] = indexHolder[0];
        } else if (Character.isDigit(c) || c == '-') {
            // Parse number
            indexHolder[0] = i;
            value = parseNumber(json, indexHolder);
            inIndexHolder[0] = indexHolder[0];
        } else if (json.startsWith("true", i)) {
            value = Boolean.TRUE;
            inIndexHolder[0] = i + 4;
        } else if (json.startsWith("false", i)) {
            value = Boolean.FALSE;
            inIndexHolder[0] = i + 5;
        } else if (json.startsWith("null", i)) {
            value = null;
            inIndexHolder[0] = i + 4;
        } else {
            throw new IllegalArgumentException("Unexpected character in JSON: " + c);
        }
        return value;
    }

    /**
     * Parse a JSON object
     * A JSON object is a key(string) and value(JSON value) map
     * @param json the string containing an exact object which is enclosed by brace. start from inIndexHolder[i] and end at end.
     * @param end the excluded end index of the object.
     * @param inIndexHolder array of one int which stores the starting index and will be updated to the position after the object
     * @return the parsed object
     */
    public static Object parseObject(String json, int end, int[] inIndexHolder) {
        int i = inIndexHolder[0];
        if (json.charAt(inIndexHolder[0]) != '{') {
            throw new IllegalArgumentException("Expected '{' at position " + inIndexHolder[0]);
        }
        i++; // Move past the opening brace

        Map<String, Object> result = new HashMap<>();
        int[] indexHolder = new int[1];
        while (i < end) {
            // Find key
            if (json.charAt(i) != '"') {
                i++;
                continue;
            }
            indexHolder[0] = i;
            String key = parseString(json, indexHolder);
            i = indexHolder[0];

            // Find colon
            while (i < end && json.charAt(i) != ':') {
                i++;
            }
            i++;

            // Skip whitespace
            while (i < end && Character.isWhitespace(json.charAt(i))) {
                i++;
            }

            // Find value
            if (i < end) {
                indexHolder[0] = i;
                Object value = parseValue(json, indexHolder);
                result.put(key, value);
                i = indexHolder[0];

                // Skip to next pair
                while (i < end && json.charAt(i) != ',') {
                    i++;
                }
                // skip the comma
                if (i < end) {
                    i++;
                }
            }
        }
        inIndexHolder[0] = i;
        return result;
    }

    /**
     * Parse a JSON array
     * A JSON array is an array of JSON value
     * @param json the string containing an exact array which is enclosed by bracket. start from inIndexHolder[i] and end at end.
     * @param end the excluded end index of the array.
     * @param inIndexHolder array of one int which stores the starting index and will be updated to the position after the object
     * @return the parsed array
     */
    public static Object parseArray(String json, int end, int[] inIndexHolder) {
        int i = inIndexHolder[0];
        if (json.charAt(i) != '[') {
            throw new IllegalArgumentException("Expected '[' at position " + i);
        }
        i++; // Move past the opening bracket

        List<Object> result = new ArrayList<>();
        if (i == end - 1 && json.charAt(i) == ']') {
            return result;
        }

        int[] indexHolder = new int[1];
        while (i < end) {
            // Skip whitespace
            while (i < end && Character.isWhitespace(json.charAt(i))) {
                i++;
            }

            // Find value
            if (i < end) {
                indexHolder[0] = i;
                Object value = parseValue(json, indexHolder);
                result.add(value);
                i = indexHolder[0];

                // Skip to next pair
                while (i < end && json.charAt(i) != ',') {
                    i++;
                }
                // skip the comma
                if (i < end) {
                    i++;
                }
            }
        }
        inIndexHolder[0] = i;
        return result;
    }

    /**
     * Parse a JSON string
     * @param json the string containing the json string ex: "[\"testString\", \"key\"]",
     * @param indexHolder array of one int which stores the starting index and will be updated to the position after the object
     * @return the parsed string
     */
    public static String parseString(String json, int[] indexHolder) {
        int i = indexHolder[0];
        if (json.charAt(i) != '"') {
            throw new IllegalArgumentException("Expected '\"' at position " + i);
        }
        i++; // Move past the opening quote

        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\"') {
                // the end of string
                i++;
                indexHolder[0] = i;
                return sb.toString();
            } else if (c == '\\') {
                // spot the backslash, handle the escape sequence
                i++;
                char next = json.charAt(i);
                if (next == '\"') {
                    sb.append('\"');
                } else if (next == '\\') {
                    sb.append('\\');
                } else if (next == '/') {
                    sb.append('/');
                } else if (next == 't') {
                    sb.append('\t');
                } else if (next == 'r') {
                    sb.append('\r');
                } else if (next == 'n') {
                    sb.append('\n');
                } else if (next == 'b') {
                    sb.append('\b');
                } else if (next == 'f') {
                    sb.append('\f');
                } else {
                    throw new IllegalArgumentException("Illegal escape sequence");
                }
            } else {
                sb.append(c);
            }
            i++;
        }
        throw new IllegalArgumentException("Unterminated string starting at position " + indexHolder[0]);
    }

    /**
     * Parse a JSON number
     * @param json the string containing the number
     * @param indexHolder array of one int which stores the starting index and will be updated to the position after the number
     * @return the parsed number as Double or Integer
     */
    public static Number parseNumber(String json, int[] indexHolder) {
        int i = indexHolder[0];
        char c = json.charAt(i);

        if (!Character.isDigit(c) && c != '-') {
            throw new IllegalArgumentException("Expected digit or '-' at position " + i);
        }

        int numStart = i;
        while (i < json.length()
               && (Character.isDigit(json.charAt(i))
                   || json.charAt(i) == '.'
                   || json.charAt(i) == '+'
                   || json.charAt(i) == '-'
                   || json.charAt(i) == 'E'
                   || json.charAt(i) == 'e')) {
            i++;
        }

        String numStr = json.substring(numStart, i);
        indexHolder[0] = i; // Update the index holder to position after number

        if (numStr.contains(".") || numStr.contains("E") || numStr.contains("e")) {
            return Double.parseDouble(numStr);
        } else {
            return Integer.parseInt(numStr);
        }
    }
}
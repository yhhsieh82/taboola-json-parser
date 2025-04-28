package com.taboola.json;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class JSONParserTest {

    // Tests for parse method
    @Test
    public void testParse_ValidJson_ReturnsCorrectResult() {
        String input = "{\"debug\":\"on\",\"window\":{\"title\":\"sample\",\"size\":500}}";

        Map<String, Object> output = (Map<String, Object>)JSONParser.parse(input);
        assert output.get("debug").equals("on");
        assertEquals("sample", ((Map<String,Object>) output.get("window")).get("title"));
        assertEquals(500, ((Map<String,Object>) output.get("window")).get("size"));
    }

    @Test
    public void testParse_EmptyJson_ReturnsEmptyMap() {
        Object result = JSONParser.parse("{}");
        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertTrue(map.isEmpty());
    }

    // Tests for parseValue method
    @Test
    public void testParseValue_String_ReturnsCorrectValue() {
        String json = "\"test string\"";
        int[] indexHolder = new int[]{0};
        Object result = JSONParser.parseValue(json, indexHolder);
        assertEquals("test string", result);
        assertEquals(json.length(), indexHolder[0]); // Should be at the end
    }

    @Test
    public void testParseValue_Number_ReturnsCorrectValue() {
        String json = "42";
        int[] indexHolder = new int[]{0};
        Object result = JSONParser.parseValue(json, indexHolder);
        assertEquals(42, result);
        assertEquals(json.length(), indexHolder[0]);
    }

    @Test
    public void testParseValue_Decimal_ReturnsCorrectValue() {
        String json = " -3.14159";
        int[] indexHolder = new int[]{0};
        Object result = JSONParser.parseValue(json, indexHolder);
        assertEquals(-3.14159, result);
        assertEquals(json.length(), indexHolder[0]);
    }

    @Test
    public void testParseValue_True_ReturnsCorrectValue() {
        String json = "true";
        int[] indexHolder = new int[]{0};
        Object result = JSONParser.parseValue(json, indexHolder);
        assertEquals(Boolean.TRUE, result);
        assertEquals(json.length(), indexHolder[0]);
    }

    @Test
    public void testParseValue_Null_ReturnsCorrectValue() {
        String json = "null";
        int[] indexHolder = new int[]{0};
        Object result = JSONParser.parseValue(json, indexHolder);
        assertNull(result);
        assertEquals(json.length(), indexHolder[0]);
    }
} 
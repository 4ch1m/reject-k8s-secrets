package com.foobar.reject_k8s_secrets.parser;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class YamlParserTest extends TestCase {

    private static String getYamlStringFromFile(String fileName) {
        try {
            return IOUtils.toString(
                    YamlParserTest.class.getResourceAsStream(fileName),
                    "UTF-8"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testParse() {
        assertFalse(YamlParser.parse(YamlParserTest.getYamlStringFromFile("invalid.yml")));
        assertTrue(YamlParser.parse(YamlParserTest.getYamlStringFromFile("configmap.yml")));
        assertFalse(YamlParser.parse(YamlParserTest.getYamlStringFromFile("secret_with_data.yml")));
        assertFalse(YamlParser.parse(YamlParserTest.getYamlStringFromFile("secret_with_stringdata.yml")));
        assertTrue(YamlParser.parse(YamlParserTest.getYamlStringFromFile("secret_without_data.yml")));
        assertFalse(YamlParser.parse(YamlParserTest.getYamlStringFromFile("list_with_secret_and_data.yml")));
        assertTrue(YamlParser.parse(YamlParserTest.getYamlStringFromFile("list_with_secret_and_no_data.yml")));
    }
}

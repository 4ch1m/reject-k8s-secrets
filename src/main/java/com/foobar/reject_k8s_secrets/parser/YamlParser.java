package com.foobar.reject_k8s_secrets.parser;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlParser {
    public static boolean parse(String yamlString) {
        Yaml yaml = new Yaml();

        try {
            Map<String, Object> yamlMap = yaml.load(yamlString);

            if (yamlMap.containsKey("kind")) {
                List<Map<String, Object>> items = new ArrayList<>();

                if (((String)yamlMap.get("kind")).trim().equals("List")) {
                    items.addAll((List<Map<String, Object>>)yamlMap.get("items"));
                } else {
                    items.add(yamlMap);
                }

                for (Map<String, Object> item : items) {
                    if (!noSecretWithData(item)) {
                        return false;
                    }
                }
            }
        } catch (YAMLException yamlException) {
            return false;
        }

        return true;
    }

    private static boolean noSecretWithData(Map<String, Object> yamlMap) {
        String kind = (String) yamlMap.get("kind");

        if (kind.trim().equals("Secret")) {
            if (yamlMap.containsKey("data") || yamlMap.containsKey("stringData")) {
                return false;
            }
        }

        return true;
    }
}

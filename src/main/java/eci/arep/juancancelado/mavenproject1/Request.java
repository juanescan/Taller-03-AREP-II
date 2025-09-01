/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eci.arep.juancancelado.mavenproject1;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author juane
 */
public class Request {
        private final String method;
        private final String rawPath;
        private final String path;
        private final Map<String, String> query;

        Request(String method, String rawPath) {
            this.method = method;
            this.rawPath = rawPath;
            String[] parts = rawPath.split("\\?", 2);
            this.path = parts[0];
            this.query = parseQuery(parts.length > 1 ? parts[1] : "");
        }
        public String method() { return method; }
        public String path() { return path; }
        public String rawPath() { return rawPath; }
        public String getValues(String key) { return query.getOrDefault(key, ""); }

        private Map<String,String> parseQuery(String q) {
            Map<String,String> m = new HashMap<>();
            if (q == null || q.isEmpty()) return m;
            for (String pair : q.split("&")) {
                String[] kv = pair.split("=",2);
                if (kv.length==2) {
                    m.put(urlDecode(kv[0]), urlDecode(kv[1]));
                }
            }
            return m;
        }
        private String urlDecode(String s) {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        }
    }
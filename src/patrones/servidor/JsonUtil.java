package patrones.servidor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilidad liviana de serialización y parseo JSON (UTF-8, Locale.US) sin dependencias externas.
 * Soporta objetos planos y arrays de objetos (como items del carrito de compras).
 */
public class JsonUtil {

    public static String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String str(String s) {
        return "\"" + esc(s) + "\"";
    }

    public static String stringArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(str(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Parsea un objeto JSON plano o con una clave "items" de tipo array de objetos.
     */
    public static Map<String, String> parseObject(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null) return map;
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) return map;

        String inner = trimmed.substring(1, trimmed.length() - 1).trim();
        List<String> pairs = splitTopLevel(inner, ',');
        for (String pair : pairs) {
            int colon = findTopLevelChar(pair, ':');
            if (colon <= 0) continue;
            String k = unquote(pair.substring(0, colon).trim());
            String vRaw = pair.substring(colon + 1).trim();
            if (vRaw.startsWith("[") || vRaw.startsWith("{")) {
                map.put(k, vRaw);
            } else {
                map.put(k, unquote(vRaw));
            }
        }
        return map;
    }

    public static List<Map<String, String>> parseObjectArray(String arrayJson) {
        List<Map<String, String>> result = new ArrayList<>();
        if (arrayJson == null) return result;
        String trimmed = arrayJson.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return result;
        String inner = trimmed.substring(1, trimmed.length() - 1).trim();
        if (inner.isEmpty()) return result;

        for (String elem : splitTopLevel(inner, ',')) {
            String t = elem.trim();
            if (t.startsWith("{") && t.endsWith("}")) {
                result.add(parseObject(t));
            }
        }
        return result;
    }

    private static List<String> splitTopLevel(String s, char delimiter) {
        List<String> parts = new ArrayList<>();
        int depthBrace = 0;
        int depthBracket = 0;
        boolean inQuotes = false;
        boolean escaped = false;
        int start = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\' && inQuotes) {
                escaped = true;
                continue;
            }
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{') depthBrace++;
                else if (c == '}') depthBrace--;
                else if (c == '[') depthBracket++;
                else if (c == ']') depthBracket--;
                else if (c == delimiter && depthBrace == 0 && depthBracket == 0) {
                    parts.add(s.substring(start, i));
                    start = i + 1;
                }
            }
        }
        if (start < s.length()) {
            parts.add(s.substring(start));
        }
        return parts;
    }

    private static int findTopLevelChar(String s, char target) {
        boolean inQuotes = false;
        boolean escaped = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (escaped) { escaped = false; continue; }
            if (c == '\\' && inQuotes) { escaped = true; continue; }
            if (c == '"') inQuotes = !inQuotes;
            else if (!inQuotes && c == target) return i;
        }
        return -1;
    }

    private static String unquote(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.equals("null")) return "";
        if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) {
            t = t.substring(1, t.length() - 1);
            StringBuilder sb = new StringBuilder(t.length());
            for (int i = 0; i < t.length(); i++) {
                char c = t.charAt(i);
                if (c == '\\' && i + 1 < t.length()) {
                    char next = t.charAt(++i);
                    switch (next) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        default: sb.append(next); break;
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return t;
    }
}

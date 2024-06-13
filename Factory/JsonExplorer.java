package FunnyJsonExplorer.Factory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class JsonExplorer {
    private Style style;
    private IconFamily iconFamily;

    // 构造函数，通过工厂方法模式创建Style和IconFamily
    public JsonExplorer(StyleFactory styleFactory, IconFamilyFactory iconFamilyFactory) {
        this.style = styleFactory.createStyle();
        this.iconFamily = iconFamilyFactory.createIconFamily();
    }

    // 显示JSON内容的方法
    public void show(Map<String, Object> json) {
        style.draw(json, iconFamily);
    }

    // 加载JSON文件的方法
    private Map<String, Object> load(String filePath) throws IOException {
        // 读取文件内容并转换为字符串
        String content = new String(Files.readAllBytes(new File(filePath).toPath()));
        content = content.trim();

        // 检查内容是否为JSON对象
        if (content.startsWith("{") && content.endsWith("}")) {
            // 去掉大括号并解析JSON对象
            content = content.substring(1, content.length() - 1).trim();
            return parseJsonObject(content);
        }

        return new HashMap<>();
    }

    // 解析JSON对象的辅助方法
    private Map<String, Object> parseJsonObject(String content) {
        Map<String, Object> map = new HashMap<>();
        int braceCount = 0;
        int start = 0;
        boolean parsingKey = true;
        String currentKey = null;

        // 遍历字符串中的每个字符
        for (int i = 0; i <= content.length(); i++) {
            char c = i < content.length() ? content.charAt(i) : ',';
            if (c == '{') braceCount++;
            if (c == '}') braceCount--;
            if (braceCount == 0 && (c == ':' || c == ',' || i == content.length())) {
                if (parsingKey) {
                    // 解析键
                    currentKey = content.substring(start, i).trim().replaceAll("\"", "");
                    parsingKey = false;
                    start = i + 1;
                } else {
                    // 解析值
                    String value = content.substring(start, i).trim().replaceAll("\"", "");
                    if (value.equals("null")) {
                        map.put(currentKey, null);
                    } else if (value.startsWith("{") && value.endsWith("}")) {
                        // 递归解析嵌套的JSON对象
                        map.put(currentKey, parseJsonObject(value.substring(1, value.length() - 1)));
                    } else {
                        map.put(currentKey, value);
                    }
                    parsingKey = true;
                    start = i + 1;
                }
            }
        }
        return map;
    }

    // 程序入口
    public static void main(String[] args) throws IOException {
        String filePath = null;
        StyleFactory styleFactory = null;
        IconFamilyFactory iconFamilyFactory = null;

        // 解析命令行参数
        for (int i = 0; i < args.length; i++) {
            if ("-f".equals(args[i]) && i < args.length - 1) {
                filePath = args[i + 1];
                i++; // 跳过下一个参数，因为它是文件路径
            } else if ("-s".equals(args[i]) && i < args.length - 1) {
                String styleArg = args[i + 1];
                if ("tree".equals(styleArg)) {
                    styleFactory = new TreeFactory();
                } else if ("rectangle".equals(styleArg)) {
                    styleFactory = new RectangleStyleFactory();
                } else {
                    // 处理未知的样式参数
                    System.err.println("未知的样式: " + styleArg);
                    return;
                }
                i++; // 跳过下一个参数，因为它是样式参数
            } else if ("-i".equals(args[i]) && i < args.length - 1) {
                String iconFamilyArg = args[i + 1];
                if ("default".equals(iconFamilyArg)) {
                    iconFamilyFactory = new DefaultIconFamilyFactory();
                } else if ("poker".equals(iconFamilyArg)) {
                    iconFamilyFactory = new PokerFaceIconFamilyFactory();
                } else {
                    // 处理未知的图标族参数
                    System.err.println("未知的图标族: " + iconFamilyArg);
                    return;
                }
                i++; // 跳过下一个参数，因为它是图标族参数
            }
        }

        // 检查是否提供了必要的参数
        if (filePath == null || styleFactory == null || iconFamilyFactory == null) {
            System.err.println("用法: fje -f <json文件> -s <样式> -i <图标族>");
            return;
        }

        // 创建JsonExplorer对象并渲染JSON内容
        JsonExplorer explorer = new JsonExplorer(styleFactory, iconFamilyFactory);
        Map<String, Object> json = explorer.load(filePath);
        explorer.show(json);
    }
}

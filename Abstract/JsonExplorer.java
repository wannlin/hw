package FunnyJsonExplorer.Abstract;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonExplorer {
    private Container container;

    // 构造函数，通过工厂方法模式创建Container
    public JsonExplorer(ContainerFactory containerFactory) {
        this.container = containerFactory.createContainer();
    }

    // 显示JSON内容的方法
    public void show(Map<String, Object> json) {
        container.draw(json);
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
        ContainerFactory containerFactory = null;

        // 存储不同的ContainerFactory映射
        Map<String, Supplier<ContainerFactory>> containerFactoryMap = new HashMap<>();
        containerFactoryMap.put("treedefault", TreeDefaultContainerFactory::new);
        containerFactoryMap.put("treepoker", TreePokerContainerFactory::new);
        containerFactoryMap.put("rectangledefault", RectangleDefaultContainerFactory::new);
        containerFactoryMap.put("rectanglepoker", RectanglePokerContainerFactory::new);
        containerFactoryMap.put("treeconfig", TreeConfigContainerFactory::new);

        // 解析命令行参数
        String styleArg = "";
        String iconFamilyArg = "";
        for (int i = 0; i < args.length; i++) {
            if ("-f".equals(args[i]) && i < args.length - 1) {
                filePath = args[i + 1];
                i++; // 跳过下一个参数，因为它是文件路径
            } else if ("-s".equals(args[i]) && i < args.length - 1) {
                styleArg = args[i + 1];
                i++; // 跳过下一个参数，因为它是样式参数
            } else if ("-i".equals(args[i]) && i < args.length - 1) {
                iconFamilyArg = args[i + 1];
                i++; // 跳过下一个参数，因为它是图标族参数
            }
        }

        // 检查并获取相应的ContainerFactory
        if (containerFactoryMap.get(styleArg + iconFamilyArg) == null) {
            if (!"tree".equals(styleArg) && !"rectangle".equals(styleArg)) {
                // 处理未知的样式参数
                System.err.println("未知的样式: " + styleArg + iconFamilyArg);
                return;
            }
        } else {
            containerFactory = containerFactoryMap.get(styleArg + iconFamilyArg).get();
        }

        // 检查是否提供了必要的参数
        if (filePath == null || containerFactory == null) {
            System.err.println("用法: fje -f <json文件> -s <样式> -i <图标族>");
            return;
        }

        // 创建JsonExplorer对象并渲染JSON内容
        JsonExplorer explorer = new JsonExplorer(containerFactory);
        Map<String, Object> json = explorer.load(filePath);
        explorer.show(json);
    }
}

package FunnyJsonExplorer.Abstract;

import java.util.Map;

public class RectangleStyle extends Style {
    // 绘制JSON内容的方法
    public void draw(Map<String, Object> json, IconFamily iconFamily) {
        int isParentEnd = -1;
        printRectangle(json, 0, iconFamily, isParentEnd);
    }

    // 打印矩形框的方法
    private void printRectangle(Map<String, Object> map, int level, IconFamily iconFamily, int isParentEnd) {
        String indent = "   ".repeat(level); // 根据等级计算缩进
        int entryCount = map.size();
        int currentIndex = 0;
        int rectangleWidth = 60; // 矩形的总宽度
        int currentLen = 0;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            currentIndex++;
            boolean isLastEntry = currentIndex == entryCount;
            currentLen = 0;

            // 调整同级后续条目的缩进
            if (isParentEnd == -1) {
                indent = "\u250C\u2500\u2500"; // 起始条目符号
            } else if (isParentEnd == 1 && isLastEntry) {
                indent = "\u2514\u2500\u2500"; // 结束条目符号
            } else {
                indent = "\u2502  ".repeat(level); // 中间条目符号
            }

            // 根据数据项是叶节点还是子树来确定图标
            String icon = (entry.getValue() instanceof Map) ? iconFamily.getMiddleIcon() : iconFamily.getLeafIcon();
            String connector = indent.equals("\u2514\u2500\u2500") ? "\u2514\u2500\u2500" : "\u251C\u2500\u2500";
            if (isParentEnd == -1) {
                connector = "\u2500\u2500\u2500"; // 顶层条目符号
            }
            if (level == 0) {
                if (isParentEnd == -1) connector = "";
                isParentEnd = isLastEntry ? 1 : 0;
            }

            System.out.print(indent + connector + icon + entry.getKey()); // 打印键和图标
            currentLen += (indent + connector + icon + entry.getKey()).length();

            if (entry.getValue() instanceof Map) {
                // 如果值是Map，打印新行并递归打印子树
                String endStr = "\u2500".repeat(rectangleWidth - currentLen);
                if (indent.equals("\u250C\u2500\u2500")) {
                    endStr += "\u2510"; // 起始条目右边框符号
                } else if (indent.equals("\u2514\u2500\u2500")) {
                    endStr += "\u2518"; // 结束条目右边框符号
                } else {
                    endStr += "\u2524"; // 中间条目右边框符号
                }
                System.out.print(endStr);
                System.out.println();
                printRectangle((Map<String, Object>) entry.getValue(), level + 1, iconFamily, isLastEntry ? 1 : 0);
            } else {
                // 如果值不是Map，在同一行打印值
                if (entry.getValue() != null) {
                    System.out.print(": " + entry.getValue());
                    currentLen += 2 + entry.getValue().toString().length();
                }
                String endStr = "\u2500".repeat(rectangleWidth - currentLen);
                if (indent.equals("\u250C\u2500\u2500")) {
                    endStr += "\u2510"; // 起始条目右边框符号
                } else if (indent.equals("\u2514\u2500\u2500")) {
                    endStr += "\u2518"; // 结束条目右边框符号
                } else {
                    endStr += "\u2524"; // 中间条目右边框符号
                }
                System.out.println(endStr);
            }
        }
    }
}

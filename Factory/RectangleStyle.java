package FunnyJsonExplorer.Factory;
import java.util.Map;

public class RectangleStyle extends Style {
    @Override
    public void draw(Map<String, Object> json, IconFamily iconFamily) {
        int isParentEnd = -1;
        printRectangle(json, 0, iconFamily, isParentEnd);
    }

    private void printRectangle(Map<String, Object> map, int level, IconFamily iconFamily, int isParentEnd) {
        // 根据等级计算缩进
        String indent = "   ".repeat(level);
        int entryCount = map.size();
        int currentIndex = 0;
        int rectangleWidth = 60;
        int currentLength = 0;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            currentIndex++;
            boolean isLastEntry = currentIndex == entryCount;
            currentLength = 0;

            // 根据是否是父节点的最后一个子节点调整缩进
            if (isParentEnd == -1) {
                indent = "\u250C\u2500\u2500";
            } else if (isParentEnd == 1 && isLastEntry) {
                indent = "\u2514\u2500\u2500";
            } else {
                indent = "\u2502  ".repeat(level);
            }

            // 根据数据项是叶节点还是子树来确定图标
            String icon = (entry.getValue() instanceof Map) ? iconFamily.getMiddleIcon() : iconFamily.getLeafIcon();
            String connector = indent.equals("\u2514\u2500\u2500") ? "\u2514\u2500\u2500" : "\u251C\u2500\u2500";
            if (isParentEnd == -1) {
                connector = "\u2500\u2500\u2500";
            }
            if (level == 0) {
                if (isParentEnd == -1) connector = "";
                isParentEnd = isLastEntry ? 1 : 0;
            }

            // 打印键和值（如果有），以及图标和连接符
            System.out.print(indent + connector + icon + entry.getKey());
            currentLength += (indent + connector + icon + entry.getKey()).length();

            if (entry.getValue() instanceof Map) {
                // 如果值是一个Map，则打印换行符并递归打印子树
                String endStr = "\u2500".repeat(rectangleWidth - currentLength);
                if (indent.equals("\u250C\u2500\u2500")) {
                    endStr += "\u2510";
                } else if (indent.equals("\u2514\u2500\u2500")) {
                    endStr += "\u2518";
                } else {
                    endStr += "\u2524";
                }
                System.out.print(endStr);
                System.out.println();

                if (isParentEnd == 1 && isLastEntry) {
                    printRectangle((Map<String, Object>) entry.getValue(), level + 1, iconFamily, 1);
                } else {
                    printRectangle((Map<String, Object>) entry.getValue(), level + 1, iconFamily, 0);
                }
            } else {
                // 如果值不是Map，则在同一行打印值
                if (entry.getValue() != null) {
                    System.out.print(": " + entry.getValue());
                    currentLength += 2 + entry.getValue().toString().length();
                }
                String endStr = "\u2500".repeat(rectangleWidth - currentLength);
                if (indent.equals("\u250C\u2500\u2500")) {
                    endStr += "\u2510";
                } else if (indent.equals("\u2514\u2500\u2500")) {
                    endStr += "\u2518";
                } else {
                    endStr += "\u2524";
                }
                System.out.println(endStr);
            }
        }
    }
}

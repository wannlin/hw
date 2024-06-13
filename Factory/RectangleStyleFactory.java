package FunnyJsonExplorer.Factory;

public class RectangleStyleFactory implements StyleFactory {
    public Style createStyle() {
        return new RectangleStyle();
    }
}

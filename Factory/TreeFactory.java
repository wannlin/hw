package FunnyJsonExplorer.Factory;

public class TreeFactory implements StyleFactory {
    public Style createStyle() {
        return new Tree();
    }
}


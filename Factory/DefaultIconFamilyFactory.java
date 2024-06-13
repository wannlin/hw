package FunnyJsonExplorer.Factory;

public class DefaultIconFamilyFactory implements IconFamilyFactory {
    public IconFamily createIconFamily() {
        return new DefaultIconFamily();
    }
}

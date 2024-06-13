package FunnyJsonExplorer.Abstract;

public class TreeDefaultContainerFactory implements ContainerFactory{
    public Container createContainer(){
        Style style = new TreeStyle();
        IconFamily icon = new DefaultIconFamily();
        return new Container(style, icon);
    }
}

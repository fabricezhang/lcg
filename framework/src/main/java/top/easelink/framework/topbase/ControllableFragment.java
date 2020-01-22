package top.easelink.framework.topbase;

public interface ControllableFragment {

    default boolean isControllable() {
        return false;
    }
}

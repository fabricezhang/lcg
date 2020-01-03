package top.easelink.framework.topbase;

public interface ControllableFragment {

    String TAG_PREFIX = "CONTROL-";

    default boolean isControllable() {
        return true;
    }
}

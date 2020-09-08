package cn.noblel.threadhook;

/**
 * @author noblel
 */
public class ThreadHook {

    static {
        System.loadLibrary("thread-hook");
    }

    native public static boolean doHook();

    native public static boolean doUnHook();

}

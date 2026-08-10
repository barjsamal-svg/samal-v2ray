package libv2ray;
public class Libv2ray {
    public static void initV2Ray() {}
    public static String checkVersion() { return "1.0"; }
    public static void startLoop(long fd, String config, CoreCallbackHandler handler, boolean predict) {}
    public static void stopLoop() {}
    public static void measureDelay(String config, String url, CoreCallbackHandler handler) {}
    public static String queryAllOutboundTrafficStats() { return ""; }
    public static void registerProcessFinder(ProcessFinder finder) {}
    public static boolean isRunning() { return false; }
}

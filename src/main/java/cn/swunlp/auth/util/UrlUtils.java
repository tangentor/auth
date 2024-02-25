package cn.swunlp.auth.util;

/**
 * URL工具类
 * @author TangXi
 * @since 2024/2/1
 */

public class UrlUtils {

    public static String getPrefix(String url) {
        // 如果 URL 以斜杠开头，移除它
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        // 如果 URL 为空或只有斜杠，则返回空字符串
        if (url.isEmpty() || "/".equals(url)) {
            return "";
        }
        // 找到第一个斜杠的位置
        int index = url.indexOf("/");
        // 如果没有斜杠，整个 URL 就是前缀
        // 如果有斜杠，返回斜杠前的部分
        return index == -1 ? url : url.substring(0, index);
    }

}

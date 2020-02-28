package com.ecnu.haven.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author HavenTong
 * @date 2020/2/20 4:35 下午
 */
public class FormatUtil {
    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }
}

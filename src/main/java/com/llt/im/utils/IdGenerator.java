package com.llt.im.utils;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author llt11
 */
@Data
public class IdGenerator {
    private static final AtomicInteger id = new AtomicInteger(1000);

    public static String getId() {
        return String.valueOf(id.incrementAndGet());
    }
}

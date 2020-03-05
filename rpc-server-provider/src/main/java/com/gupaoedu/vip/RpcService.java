package com.gupaoedu.vip;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component

/**
 * public interface com.gupaoedu.vip.RpcService extends java.lang.annotation.Annotation {
 *   public abstract java.lang.Class<?> value();
 *   public abstract java.lang.String version();
 * }
 */
public @interface RpcService {
    Class<?> value();
    String version() default "";
}

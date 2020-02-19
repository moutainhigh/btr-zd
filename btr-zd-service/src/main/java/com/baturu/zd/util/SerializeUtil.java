package com.baturu.zd.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * 序列化工具类
 * @author CaiZhuliang
 * @since 2019-3-21
 */
@Slf4j
public class SerializeUtil {

    private SerializeUtil() {}

    public static <T> String serialize(T object) {
        if (null == object) {
            return StringUtils.EMPTY;
        }
        String str = StringUtils.EMPTY;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            str = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("序列化失败! error : ", e);
        } finally {
            try {
                if (null != oos) {
                    oos.close();
                }
            } catch (Exception e) {
                log.info("serialize error : ", e);
            }
        }
        return str;
    }

    public static <T> T deserialize(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        T t = null;
        byte[] bytes = Base64.getDecoder().decode(str);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            log.debug("deserialize obj = {}", obj);
            t = (T) obj;
        } catch (Exception e) {
            log.error("反序列化失败! error : ", e);
        } finally {
            try {
                if (null != ois) {
                    ois.close();
                }
            } catch (Exception e) {
                log.info("deserialize error : ", e);
            }
        }
        return t;
    }

}

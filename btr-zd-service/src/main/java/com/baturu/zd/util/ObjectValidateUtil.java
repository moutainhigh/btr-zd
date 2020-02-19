package com.baturu.zd.util;



import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/03/12
 **/
@Slf4j
public class ObjectValidateUtil {

    private static final String serialVersionUID_field = "serialVersionUID";

    private static final String GET_METHOD_PRE = "get";

    public static <P> boolean isAllFieldNull(P param) {
        if(isNull(param)) {
            return true;
        }

        try {
            Field[] fields = param.getClass().getDeclaredFields();

            for(Field field : fields) {
                String fieldName = field.getName();
                if (serialVersionUID_field.equals(fieldName)) {
                    continue;
                }

                String methodName = buildFieldMethodName(fieldName);
                Method method = param.getClass().getDeclaredMethod(methodName);
                Object returnResult = method.invoke(param);

                if(returnResult != null) {
                    // 集合类型判断是否所有元素为空
                    if(method.getReturnType().equals(List.class) || method.getReturnType().equals(Set.class)) {
                        if(!isEmpty((Collection) returnResult)) {
                            return false;
                        }
                        // TODO: 2018/12/7 判断集合大小 -- 限制入参
                        continue;
                    }

                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("参数校验异常",e);
        }
        return true;
    }

    public static <P> boolean isNull(P param) {
        return param == null;
    }

    private static final String buildFieldMethodName(String fieldName) {
        return new StringBuilder(GET_METHOD_PRE).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
    }

    private static boolean isEmpty(Collection collection) {
        if(collection == null || collection.size() == 0) {
            return true;
        }

        for(Object o : collection) {
            if(o != null) {
                return false;
            }
        }

        return true;
    }
}

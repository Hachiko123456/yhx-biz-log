package com.yhx.log.service.impl;

import com.yhx.log.annotation.MarkField;
import com.yhx.log.service.MarkFieldLogService;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author yanghuaxu
 * @date 2022/6/14 18:23
 */
@Service
@Slf4j
public class MarkFieldLogServiceImpl implements MarkFieldLogService {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String logImportField(Object target) {
        StringBuilder result = new StringBuilder("{ ");
        Field[] fields = target.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            MarkField itf = AnnotationUtils.findAnnotation(field, MarkField.class);
            if (itf == null) {
                continue;
            }
            appendFieldValue(target, field, itf, result);
            if (i != fields.length - 1) {
                result.append(", ");
            }
        }
        result.append(" } ").append("\r\n");
        return result.toString();
    }

    private void appendFieldValue(Object target, Field field, MarkField itf, StringBuilder sb) {
        ReflectionUtils.makeAccessible(field);
        Object fieldValue = ReflectionUtils.getField(field, target);
        appendFieldName(field, itf, sb);
        if (fieldValue == null) {
            sb.append("null");
            return;
        }
        if (ClassUtils.isPrimitiveOrWrapper(fieldValue.getClass()) || fieldValue.getClass().isAssignableFrom(String.class)) {
            sb.append(fieldValue);
        } else if (Date.class.isAssignableFrom(fieldValue.getClass())) {
            sb.append(SIMPLE_DATE_FORMAT.format((Date) fieldValue));
        } else if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
            Collection<?> collection = (Collection<?>) fieldValue;
            for (Object oc : collection) {
                sb.append(logImportField(oc));
            }
        } else if (Map.class.isAssignableFrom(fieldValue.getClass())) {
            Map map = (Map) fieldValue;
            Set<Map.Entry> entrySet = map.entrySet();
            for (Map.Entry entry : entrySet) {
                sb.append(logImportField(entry.getValue()));
            }
        } else {
            sb.append(logImportField(fieldValue));
        }
    }

    private void appendFieldName(Field field, MarkField itf, StringBuilder sb) {
        if (StringUtils.isEmpty(itf.value())) {
            ApiModelProperty apiModelProperty = AnnotationUtils.findAnnotation(field, ApiModelProperty.class);
            if (apiModelProperty == null) {
                sb.append(field.getName());
            } else {
                sb.append(apiModelProperty.value());
            }
        } else {
            sb.append(itf.value());
        }
        sb.append(": ");
    }
}

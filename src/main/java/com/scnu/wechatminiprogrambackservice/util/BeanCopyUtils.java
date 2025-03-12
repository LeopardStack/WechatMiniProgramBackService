package com.scnu.wechatminiprogrambackservice.util;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean拷贝工具类
 */
public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    /**
     * 复制对象
     *
     * @param source 源对象
     * @param clazz  目标对象类型
     * @return 目标对象
     */
    public static <T> T copyObject(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        T target;
        try {
            target = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
        } catch (Exception e) {
            throw new RuntimeException("Bean复制失败: " + e.getMessage());
        }

        return target;
    }

    /**
     * 复制集合
     *
     * @param sourceList 源集合
     * @param clazz      目标对象类型
     * @return 目标集合
     */
    public static <T, S> List<T> copyList(List<S> sourceList, Class<T> clazz) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>(0);
        }

        return sourceList.stream()
                .map(source -> copyObject(source, clazz))
                .collect(Collectors.toList());
    }
}
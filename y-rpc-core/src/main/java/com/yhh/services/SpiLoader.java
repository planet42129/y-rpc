package com.yhh.services;

import cn.hutool.core.io.resource.ResourceUtil;
import com.yhh.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器（支持键值对映射）
 *
 * @author hyh
 * @date 2024/5/14
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接口名 =>（key区分多个实现类 => 实现类的全类名）
     *
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象缓存实例（避免重复new）：类路径 => 对象实例，单例模式
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";


    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    // todo 按需加载，不用一次性把接口对应的实现类对象创建出来
    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }


    /**
     * 加载指定类的 SPI（Service Provider Interface）实现。
     *
     * @param loadClass 需要加载SPI实现的类，如Serializer
     * @return 返回一个映射，其中包含加载的SPI实现的键值对，键是SPI实现的标识符，值是对应的类。
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        // 扫描指定路径下所有的SPI实现，并以用户自定义的SPI优先
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) { // 遍历扫描路径
            String path = scanDir + loadClass.getName();//META-INF/rpc/system/com.yhh.serializer.Serializer
            List<URL> resources = ResourceUtil.getResources(path); // 获取资源文件列表
            // 读取并处理每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) { // 逐行读取资源文件
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) { // 处理有效的行
                            String key = strArray[0];
                            String classPathName = strArray[1];
                            keyClassMap.put(key, Class.forName(classPathName)); // 加载类并添加到映射中
                        }
                    }
                } catch (Exception e) {
                    log.debug("spi resource load error", e); // 处理加载异常
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap); // 将加载的SPI映射保存至全局映射中
        return keyClassMap; // 返回加载的SPI实现映射
    }

    /**
     * 获取某个接口的实例
     *
     * @param tClass 接口名称
     * @param key 接口对应的某个实现类的名称
     * @param <T> 泛型
     * @return 返回某个实现类的实例对象
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();//接口的全类名
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            //todo 自定义异常
            throw new RuntimeException(String.format("SpiLoader not load %s type", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader's %s not exist key=%s type", tClassName, key));
        }
        // 获取到要加载的实现类的Class实例
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型
        // 获取实现类的实例的全类名
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s failed to newInstance", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    private SpiLoader() {

    }


    public static void main(String[] args) {
        loadAll();
        System.out.println(loaderMap);
        Serializer serializer = getInstance(Serializer.class, "jdk");
        log.info(serializer.toString());
    }

}

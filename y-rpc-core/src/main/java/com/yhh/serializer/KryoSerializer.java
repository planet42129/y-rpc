package com.yhh.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author hyh
 * @date 2024/5/13
 */
public class KryoSerializer implements Serializer {
    /**
     * kryo 线程不安全，
     * 使用ThreadLocal来保证每个线程都只有一个Kryo实例，以避免多线程环境下的并发问题。
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 设置动态动态序列化和反序列化类，不提前注册所有类（可能有安全问题）
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    //todo 在结束之后要remove掉ThreadLocal中的kyro序列化器

    //如果 Kryo 是一个全局的序列化器实例，
    //在程序结束时手动释放资源。你可以在程序结束时调用一个方法来释放 Kryo 实例所占用的资源
    /*
    public class ShutdownHandler {

    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 在程序结束时释放 Kryo 实例所占用的资源
            // 例如，可以调用 Kryo 实例的 close() 方法
            // kryo.close();
        }));
    }

}

     */

    /*

    // 添加一个静态内部类来处理生命周期
    private static class KryoThreadLocal extends ThreadLocal<Kryo> {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            return kryo;
        }

        // 添加removeIfUnused方法，这可以在不再需要时调用
        public void removeIfUnused() {
            super.remove(); // 移除当前线程的Kryo实例
        }
    }

    // 如果在Web应用中，可以在servlet上下文销毁时调用此方法
    public static void cleanup() {
        // 检查当前线程是否仍然在使用Kryo实例
        if (KRYO_THREAD_LOCAL.get() != null) {
            KRYO_THREAD_LOCAL.removeIfUnused();
        }
    }
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        KRYO_THREAD_LOCAL.get().writeObject(output, object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        T result = KRYO_THREAD_LOCAL.get().readObject(input, type);
        input.close();
        return result;
    }
}

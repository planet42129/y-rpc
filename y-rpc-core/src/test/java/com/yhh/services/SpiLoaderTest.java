package com.yhh.services;

import com.yhh.serializer.JdkSerializer;
import com.yhh.serializer.KryoSerializer;
import com.yhh.serializer.Serializer;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Map;

public class SpiLoaderTest {

    @Test
    public void testLoad() {
        // Arrange
        Class<?> loadClass = Serializer.class;

        // Act
        Map<String, Class<?>> result = SpiLoader.load(loadClass);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("jdk"));
        assertTrue(result.containsKey("kryo"));
        assertEquals(JdkSerializer.class, result.get("jdk"));
        assertEquals(KryoSerializer.class, result.get("kro"));
    }


}

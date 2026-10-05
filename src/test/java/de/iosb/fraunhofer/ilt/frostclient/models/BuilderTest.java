/*
 * Copyright (C) 2024 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.iosb.fraunhofer.ilt.frostclient.models;

import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityProperty;
import de.fraunhofer.iosb.ilt.frostclient.models.CommonProperties.Builder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

public abstract class BuilderTest {

    protected void assertEntityProperty(Entity entity, EntityProperty<?> property, Object expectedValue) {
        Assertions.assertEquals(expectedValue, entity.getProperty(property), "Property " + property.getName());
    }

    protected <T extends Builder<T>> void assertBuilderReturnsSelf(T builder) {
        T result = builder.getThis();
        Assertions.assertSame(builder, result, "Builder method should return the builder instance for fluent interface");
    }

    protected void assertThrowsIllegalArgumentException(Executable executable) {
        IllegalArgumentException exception = null;
        try {
            executable.execute();
        } catch (IllegalArgumentException e) {
            exception = e;
        } catch (Throwable t) {
        }
        if (exception == null) {
            throw new AssertionError("Expected IllegalArgumentException to be thrown");
        }
    }

}

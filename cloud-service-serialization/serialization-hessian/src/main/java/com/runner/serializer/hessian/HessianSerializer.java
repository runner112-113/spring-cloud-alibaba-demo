/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.runner.serializer.hessian;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.runner.misc.utils.ByteUtils;
import com.runner.serializer.api.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Serializer implement by hessian.
 *
 * */
public class HessianSerializer implements Serializer {
    
    private static final String NAME = "Hessian";
    
    private SerializerFactory serializerFactory = new NacosHessianSerializerFactory();
    
    public HessianSerializer() {
    }
    
    @Override
    public <T> T deserialize(byte[] data) {
        return deseiralize0(data);
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        T result = deserialize(data);
        if (result == null) {
            return null;
        }
        if (cls.isAssignableFrom(result.getClass())) {
            return result;
        }
        throw new NacosDeserializationException(cls, new ClassCastException(
                "%s cannot be cast to %s".format(result.getClass().getCanonicalName(), cls.getCanonicalName())));
    }
    
    @Override
    public <T> T deserialize(byte[] data, Type type) {
        return deserialize(data);
    }
    
    private <T> T deseiralize0(byte[] data) {
        if (ByteUtils.isEmpty(data)) {
            return null;
        }
        
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        input.setSerializerFactory(serializerFactory);
        Object resultObject;
        try {
            resultObject = input.readObject();
            input.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred when Hessian serializer decode!", e);
        }
        return (T) resultObject;
    }
    
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(obj);
            output.close();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred when Hessian serializer encode!", e);
        }
        
        return byteArray.toByteArray();
    }
    
    @Override
    public String name() {
        return NAME;
    }
    
}

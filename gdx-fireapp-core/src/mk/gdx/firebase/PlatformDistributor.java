/*
 * Copyright 2017 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mk.gdx.firebase;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import mk.gdx.firebase.exceptions.PlatformDistributorException;

/**
 * Provides clear and simple way to do platform specific code.
 * <p>
 * Using manual:
 * <p>
 * First of all create an Interface that contains the methods you want to share between LibGDX modules (android, ios)<p>
 * then create {@code PlatformDistributor} which will be accessible from all modules.
 * <p>
 * {@code
 * class AndroidAndIosManager extends PlatformDistributor<YourInterface>{
 * }
 * }
 * <p>
 * Now create classes that implements same interface in android and ios modules:
 * <p>
 * {@code
 * class MyAndroidLib implements YourInterface{}
 * class MyIOSLib implements YourInterface{}
 * }
 * <p>
 * Last step is telling {@code PlatformDistributor} which classes it should use on each platform,<p>
 * you can do this by implementing {@link #getAndroidClassName()} and {@link #getIOSClassName()} methods.
 */
public abstract class PlatformDistributor<T> {

    protected T platformObject;

    /**
     * Creates platform specific object by reflection.
     * <p>
     * Uses class names given by {@link #getAndroidClassName()} and {@link #getIOSClassName()}<p>
     * If environment is different than Android or iOS creates mock class provided by {@link Proxy#newProxyInstance(ClassLoader, Class[], InvocationHandler)}
     *
     * @throws PlatformDistributorException Throws when something is wrong with environment
     */
    @SuppressWarnings("unchecked")
    protected PlatformDistributor() throws PlatformDistributorException
    {
        String className = null;
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            className = getAndroidClassName();
        } else if (Gdx.app.getType() == Application.ApplicationType.iOS) {
            className = getIOSClassName();
        } else {
            Class interfaceClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            platformObject = (T) Proxy.newProxyInstance(
                    interfaceClass.getClassLoader(),
                    new Class[]{interfaceClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
                        {
                            return null;
                        }
                    }
            );
            return;
        }
        try {
            Class objClass = ClassReflection.forName(className);
            platformObject = (T) ClassReflection.getConstructor(objClass).newInstance();
        } catch (ReflectionException e) {
            e.printStackTrace();
            throw new PlatformDistributorException("Something wrong with environment");
        }
    }

    /**
     * Sets mock representation of platform distribution.
     * <p>
     * Useful when it is need to run application on not-supported platform.
     *
     * @param mockObject Mock representation of platform distribution object - {@code T}
     */
    public void setMockObject(T mockObject)
    {
        platformObject = mockObject;
    }

    /**
     * Gives class name of object that will be create when application running on ios platform.
     *
     * @return Class name with package of the ios-moe module distribution object.
     */
    protected abstract String getIOSClassName();

    /**
     * Gives class name of object that will be create when application running on android platform.
     *
     * @return Class name with package of the android module distribution object.
     */
    protected abstract String getAndroidClassName();
}

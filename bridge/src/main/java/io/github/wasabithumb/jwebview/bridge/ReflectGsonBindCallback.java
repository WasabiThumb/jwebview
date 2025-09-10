package io.github.wasabithumb.jwebview.bridge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static io.github.wasabithumb.jwebview.bridge.ForwardingGsonBindCallback.GSON;

@ApiStatus.Internal
final class ReflectGsonBindCallback implements GsonBindCallback {

    private final Object instance;
    private final String methodName;

    ReflectGsonBindCallback(@NotNull Object instance, @NotNull String methodName) {
        this.instance = instance;
        this.methodName = methodName;
    }

    //

    @Override
    public @Nullable JsonElement invoke(@NotNull JsonArray array) throws Exception {
        final int n = array.size();
        final Method m = this.resolveMethod(n);

        final Type[] types = m.getGenericParameterTypes();
        assert types.length == n;

        final Object[] args = new Object[n];
        for (int i = 0; i < n; i++) {
            args[i] = GSON.fromJson(array.get(i), types[i]);
        }

        Object out;
        try {
            out = m.invoke(this.instance, args);
        } catch (InvocationTargetException | ExceptionInInitializerError e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) throw (Exception) cause;
            throw e;
        }

        if (out == null) return null;
        return GSON.toJsonTree(out, m.getGenericReturnType());
    }

    private @NotNull Method resolveMethod(int nargs) {
        Queue<Class<?>> queue = new LinkedList<>();
        queue.add(this.instance.getClass());

        Class<?> next;
        while ((next = queue.poll()) != null) {
            Method[] methods = next.getDeclaredMethods();
            Method candidate = null;

            for (Method method : methods) {
                if (!this.methodName.equals(method.getName())) continue;
                if (method.getParameterCount() != nargs) continue;
                if (candidate == null) {
                    candidate = method;
                } else {
                    throw new IllegalStateException("Multiple candidates found for \"" + this.methodName +
                            "\" with " + nargs + " arguments");
                }
            }
            if (candidate != null) {
                try {
                    candidate.setAccessible(true);
                } catch (Exception ignored) { }
                return candidate;
            }

            Class<?> superType = next.getSuperclass();
            if (superType != null) queue.add(superType);
            queue.addAll(Arrays.asList(next.getInterfaces()));
        }

        throw new IllegalStateException("No candidate found for \"" + this.methodName + "\" with " +
                nargs + " arguments");
    }

}

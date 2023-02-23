package io.github.slince.expression;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyAccessor {

    // Builtin types: objects of builtin type do not participate in recursive search;
    private static final List<Class<?>> BUILTIN_TYPES = ListUtils.union(
            new ArrayList<>(MethodKey.PRIMITIVE_TYPES.keySet()),
            new ArrayList<>(MethodKey.PRIMITIVE_TYPES.values())
    );

    static {
        BUILTIN_TYPES.add(String.class);
        BUILTIN_TYPES.add(Date.class);
        BUILTIN_TYPES.add(BigDecimal.class);
        BUILTIN_TYPES.add(Void.class);
    }

    public static final PropertyAccessor INSTANCE = new PropertyAccessor();

    /**
     * Class definition map.
     */
    private final Map<Class<?>, ClassDefinition<?>> definitions = new ConcurrentHashMap<>();

    /**
     * Search for specified property in descendants
     * @param obj the target object
     * @param property the property name
     * @return results
     */
    public List<Object> search(@NonNull Object obj, @NonNull String property) {
        return search(obj, property, new IdentityHashMap<>());
    }

    /**
     * Search for specified property in descendants
     * @param obj the target object
     * @param property the property name
     * @param visited the visited objects
     * @return results
     */
    @SuppressWarnings("unchecked")
    public List<Object> search(@NonNull Object obj, @NonNull String property, Map<Object, Object> visited){
        if (visited.containsKey(obj) || BUILTIN_TYPES.contains(obj.getClass())) {
            return Collections.emptyList();
        }
        visited.put(obj, obj);
        ClassDefinition<?> definition = getDefinition(obj.getClass());
        return definition.getFields().values().stream().flatMap(def -> {
            boolean isBuiltin = def.getIsBuiltin();
            // Read if the property name matches，
            if (def.getName().equals(property)) {
                Object value = readObjectProperty(obj, def);
                if (isBuiltin) {
                    return Stream.of(value);
                }
                return ListUtils.union(search(value, property, visited), Collections.singletonList(value)).stream();
            }
            // Ignore if it is builtin type.
            if (isBuiltin) {
                return Stream.empty();
            }
            Object value = readObjectProperty(obj, def);
            // Ignore if null
            if (Objects.nonNull(value)) {
                Stream<Object> stream;
                if (value.getClass().isArray()) {
                    stream = Arrays.stream((Object[])value);
                } else if (value instanceof Collection) {
                    stream = ((Collection<Object>) value).stream();
                } else if (value instanceof Map) {
                    stream = ((Map<Object, Object>) value).values().stream();
                } else {
                    stream = Stream.of(value);
                }
                return stream.filter(v -> Objects.nonNull(v) && !BUILTIN_TYPES.contains(v.getClass())).flatMap(i -> search(i, property, visited).stream());
            }
            return Stream.empty();
        }).collect(Collectors.toList());
    }

    /**
     * Read the specified property value of the object.
     * @param obj the target（object、array、list、map）
     * @param property the property name
     * @return result
     */
    public Object read(@NonNull Object obj, @NonNull Object property) {
        Class<?> type = obj.getClass();
        if (type.isArray()) {
            if (property instanceof Integer) {
                return Array.get(obj, (int) property);
            } else if (property instanceof String && ((String) property).equalsIgnoreCase("length")) {
                return Array.getLength(obj);
            }
            throw new RuntimeException(String.format("Unsupported property %s", property));
        }
        if (obj instanceof List && property instanceof Integer) {
            return ((List<?>)obj).get((int)property);
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>)obj).get(property);
        }
        if (obj instanceof PropertyGetter) {
            return ((PropertyGetter)obj).getProperty((String)property);
        }
        FieldDefinition definition = getDefinition(obj.getClass()).getFieldDefinition((String) property);
        return readObjectProperty(obj, definition);
    }

    /**
     * Read the specified property value of the object.
     * @param obj the target object
     * @param definition the property definition
     * @return result
     */
    private Object readObjectProperty(@NonNull Object obj, FieldDefinition definition){
        if (definition.getIsPublic()) {
            return ReflectionUtils.getField(definition.getField(), obj);
        }
        Objects.requireNonNull(definition.getReadMethod(), String.format("Cannot find read method for %s in class %s", definition.getName(), obj.getClass().getName()));
        return ReflectionUtils.invokeMethod(definition.getReadMethod(), obj);
    }

    /**
     * Modify the specified property value of the object.
     * @param obj the target（object、array、list、map）
     * @param property the property name
     * @param value new value.
     */
    @SuppressWarnings("unchecked")
    public void write(@NonNull Object obj, @NonNull Object property, Object value) {
        Class<?> type = obj.getClass();
        if (type.isArray()) {
             Array.set(obj, (int)property, value);
             return;
        }
        if (obj instanceof List) {
            ((List<Object>)obj).set((int)property, value);
            return;
        }
        if (obj instanceof Map) {
            ((Map<Object, Object>)obj).put(property, value);
            return;
        }
        FieldDefinition definition = getDefinition(obj.getClass()).getFieldDefinition((String) property);
        if (definition.getIsPublic()) {
            ReflectionUtils.setField(definition.getField(), obj, value);
        } else {
            Objects.requireNonNull(definition.getWriteMethod(), String.format("Cannot find write method for %s in class %s", property, type.getName()));
            ReflectionUtils.invokeMethod(definition.getWriteMethod(), obj, value);
        }
    }

    /**
     * Call the specified method of the object
     * @param obj the target object
     * @param methodName method name
     * @param arguments arguments
     * @return the result
     */
    public Object call(@NonNull Object obj, String methodName, Object ...arguments){
        MethodKey methodKey = new MethodKey(methodName, arguments);
        Method method = getDefinition(obj.getClass()).getMethods().getMethod(methodKey);
        Objects.requireNonNull(method, String.format("Cannot find matched method %s in class %s", methodName, obj.getClass().getName()));
        return ReflectionUtils.invokeMethod(method, obj, arguments);
    }

    private ClassDefinition<?> getDefinition(Class<?> type){
        if (definitions.containsKey(type)) {
            return definitions.get(type);
        }
        return definitions.computeIfAbsent(type, v -> createDefinition(type));
    }

    private <T> ClassDefinition<T> createDefinition(Class<T> type) {
        Map<String, FieldDefinition> fields = new HashMap<>();
        // collect public properties,
        ReflectionUtils.doWithFields(type, field -> {
            boolean isBuiltin = BUILTIN_TYPES.contains(field.getType());
            FieldDefinition definition = new FieldDefinition(field.getName(), field, true, isBuiltin, null, null);
            fields.put(field.getName(), definition);
        }, v -> Modifier.isPublic(v.getModifiers()));
        // bean property
        for (PropertyDescriptor propertyDescriptor : ReflectUtils.getBeanProperties(type)) {
            String name = propertyDescriptor.getName();
            if (fields.containsKey(name)) {
                continue;
            }
            Field field = ReflectionUtils.findField(type, name);
            boolean isBuiltin = false;
            if (Objects.nonNull(propertyDescriptor.getReadMethod())) {
                isBuiltin = BUILTIN_TYPES.contains(propertyDescriptor.getReadMethod().getReturnType());
            } else if (Objects.nonNull(field)) {
                isBuiltin = BUILTIN_TYPES.contains(field.getType());
            }
            FieldDefinition definition = new FieldDefinition(name, field, false, isBuiltin, propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod());
            fields.put(name, definition);
        }
        return new ClassDefinition<>(type, fields, new MethodMap(type));
    }

    @Getter
    @RequiredArgsConstructor
    public static class ClassDefinition<T>{
        private final Class<T> type;
        private final Map<String, FieldDefinition> fields;

        private final MethodMap methods;

        /**
         * Returns the definition of the specified field name
         * @param name field name
         * @return field definition
         */
        public FieldDefinition getFieldDefinition(String name){
            if (!fields.containsKey(name)) {
                throw new RuntimeException(String.format("the field %s in class %s is not exist", name, type.getName()));
            }
            return fields.get(name);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class FieldDefinition{
        private final String name;
        private final Field field;
        private final Boolean isPublic;
        private final Boolean isBuiltin;
        private final Method readMethod;
        private final Method writeMethod;
    }
}

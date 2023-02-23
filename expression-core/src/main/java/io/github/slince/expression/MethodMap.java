package io.github.slince.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link <a href="https://github.com/apache/commons-jexl/blob/master/src/main/java/org/apache/commons/jexl3/internal/introspection/ClassMap.java">...</a>}
 */
final class MethodMap {

    public static Method cacheMiss() {
        try {
            return MethodMap.class.getMethod("cacheMiss");
        } catch (Exception xio) {
            // this really cant make an error...
            return null;
        }
    }

    private static final Method CACHE_MISS = cacheMiss();

    private final ConcurrentMap<MethodKey, Method> byKey = new ConcurrentHashMap<>();

    private final Map<String, Method[]> byName = new HashMap<>();

    @SuppressWarnings("LeakingThisInConstructor")
    MethodMap(Class<?> type) {
        // eagerly cache methods
        create(this, type);
    }

    /**
     * Gets the methods names cached by this map.
     * @return the array of method names
     */
    String[] getMethodNames() {
        return byName.keySet().toArray(new String[0]);
    }

    /**
     * Gets all the methods with a given name from this map.
     * @param methodName the seeked methods name
     * @return the array of methods (null or non-empty)
     */
    Method[] getMethods(final String methodName) {
        Method[] lm = byName.get(methodName);
        if (lm != null && lm.length > 0) {
            return lm.clone();
        } else {
            return null;
        }
    }

    /**
     * Find a Method using the method name and parameter objects.
     *<p>
     * Look in the methodMap for an entry.  If found,
     * it'll either be a CACHE_MISS, in which case we
     * simply give up, or it'll be a Method, in which
     * case, we return it.
     *</p>
     * <p>
     * If nothing is found, then we must actually go
     * and introspect the method from the MethodMap.
     *</p>
     * @param methodKey the method key
     * @return A Method object representing the method to invoke or null.
     * @throws MethodKey.AmbiguousException When more than one method is a match for the parameters.
     */
    Method getMethod(final MethodKey methodKey) throws MethodKey.AmbiguousException {
        // Look up by key
        Method cacheEntry = byKey.get(methodKey);
        // We looked this up before and failed.
        if (cacheEntry == CACHE_MISS) {
            return null;
        } else if (cacheEntry == null) {
            try {
                // That one is expensive...
                Method[] methodList = byName.get(methodKey.getMethod());
                if (methodList != null) {
                    cacheEntry = methodKey.getMostSpecificMethod(methodList);
                }
                if (cacheEntry == null) {
                    byKey.put(methodKey, CACHE_MISS);
                } else {
                    byKey.put(methodKey, cacheEntry);
                }
            } catch (MethodKey.AmbiguousException ae) {
                // that's a miss :-)
                byKey.put(methodKey, CACHE_MISS);
                throw ae;
            }
        }

        // Yes, this might just be null.
        return cacheEntry;
    }

    /**
     * Populate the Map of direct hits. These are taken from all the public methods
     * that our class, its parents and their implemented interfaces provide.
     * @param cache the ClassMap instance we create
     * @param classToReflect the class to cache
     */
    private static void create(MethodMap cache, Class<?> classToReflect) {
        //
        // Build a list of all elements in the class hierarchy. This one is bottom-first (i.e. we start
        // with the actual declaring class and its interfaces and then move up (superclass etc.) until we
        // hit java.lang.Object. That is important because it will give us the methods of the declaring class
        // which might in turn be abstract further up the tree.
        //
        // We also ignore all SecurityExceptions that might happen due to SecurityManager restrictions.
        //
        for (; classToReflect != null; classToReflect = classToReflect.getSuperclass()) {
            if (Modifier.isPublic(classToReflect.getModifiers())) {
                populateWithClass(cache, classToReflect);
            }
            Class<?>[] interfaces = classToReflect.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                populateWithInterface(cache, interfaces[i]);
            }
        }
        // now that we've got all methods keyed in, lets organize them by name
        if (!cache.byKey.isEmpty()) {
            List<Method> lm = new ArrayList<Method>(cache.byKey.size());
            lm.addAll(cache.byKey.values());
            // sort all methods by name
            lm.sort(Comparator.comparing(Method::getName));
            // put all lists of methods with same name in byName cache
            int start = 0;
            while (start < lm.size()) {
                String name = lm.get(start).getName();
                int end = start + 1;
                while (end < lm.size()) {
                    String walk = lm.get(end).getName();
                    if (walk.equals(name)) {
                        end += 1;
                    } else {
                        break;
                    }
                }
                Method[] lmn = lm.subList(start, end).toArray(new Method[end - start]);
                cache.byName.put(name, lmn);
                start = end;
            }
        }
    }

    /**
     * Recurses up interface hierarchy to get all super interfaces.
     * @param cache the cache to fill
     * @param iface the interface to populate the cache from
     */
    private static void populateWithInterface(MethodMap cache, Class<?> iface) {
        if (Modifier.isPublic(iface.getModifiers())) {
            populateWithClass(cache, iface);
        }
        Class<?>[] supers = iface.getInterfaces();
        for (int i = 0; i < supers.length; i++) {
            populateWithInterface(cache, supers[i]);
        }
    }

    /**
     * Recurses up class hierarchy to get all super classes.
     * @param cache the cache to fill
     * @param clazz the class to populate the cache from
     */
    private static void populateWithClass(MethodMap cache, Class<?> clazz) {
        try {
            Method[] methods = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method mi = methods[i];
                if (Modifier.isPublic(mi.getModifiers())) {
                    // add method to byKey cache; do not override
                    cache.byKey.putIfAbsent(new MethodKey(mi), mi);
                }
            }
        } catch (SecurityException se) {
            // ignore this
        }
    }
}


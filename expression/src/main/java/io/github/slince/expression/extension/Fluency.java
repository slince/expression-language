package io.github.slince.expression.extension;

import io.github.slince.expression.PropertyAccessor;
import io.github.slince.expression.PropertyGetter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Fluency implements PropertyGetter {

    private final Collection<Object> collection;

    public Fluency(Object[] array){
        collection = Arrays.asList(array);
    }

    @Override
    public Fluency getProperty(String name){
        return new Fluency(collection.stream().filter(Objects::nonNull).map(v -> PropertyAccessor.INSTANCE.read(v, name)).collect(Collectors.toList()));
    }

    public Fluency filter(Predicate<Object> predicate){
        return new Fluency(collection.stream().filter(predicate).collect(Collectors.toList()));
    }

    public Fluency nonNull(){
        return filter(Objects::nonNull);
    }

    /**
     * Collect results
     * @return result
     */
    public Collection<Object> collect(){
        return collection;
    }
}

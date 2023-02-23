package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.DelegatingContext;
import io.github.slince.expression.MapContext;
import io.github.slince.expression.extension.ListExtension;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;

@Getter
public class ArrayFilter extends FilterExpr{

    public ArrayFilter(Expr object, Expr filter) {
        super(object, filter, Collections.emptyList());
    }

    @Override
    public Object evaluate(Context context) {
        Object object = getObject().evaluate(context);
        MapContext current = new MapContext();
        DelegatingContext delegating = new DelegatingContext(Arrays.asList(current, context));
        return ListExtension.filter(object, v -> {
            current.setVar("@", v);
            Object filter = getFilter().evaluate(delegating);
            return (boolean)filter;
        });
    }
}

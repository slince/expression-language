package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.Position;

public interface Node {

    /**
     * Returns the node position.
     * @return position
     */
    Position getPosition();

    /**
     * Evaluate the node.
     * @param context the runtime context.
     * @return the result
     */
    Object evaluate(Context context);
}

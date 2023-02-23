package io.github.slince.expression;

import io.github.slince.expression.ast.Node;
import io.github.slince.expression.extension.AbstractExtension;
import io.github.slince.expression.extension.CoreExtension;
import io.github.slince.expression.extension.ListExtension;
import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;
import io.github.slince.expression.visitor.NodeTraverser;
import org.apache.commons.collections4.ListUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Evaluator
 */
public class Evaluator {

    private static final List<AbstractExtension> EXTENSIONS = Arrays.asList(new CoreExtension(), new ListExtension());

    public static final Evaluator INSTANCE = new Evaluator();

    private final Context context;
    private final NodeTraverser traverser;

    public Evaluator(List<? extends AbstractExtension> extensions, NodeTraverser traverser){
        extensions = ListUtils.union(EXTENSIONS, extensions);
        Map<String, Object> vars = extensions.stream().flatMap(v -> v.getVars().entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (p, n) -> n));
        Map<String, AbstractFilter> filters = extensions.stream().flatMap(v -> v.getFilters().stream()).collect(Collectors.toMap(AbstractFilter::getName, Function.identity(), (p, n) -> n));
        Map<String, AbstractFunction> functions = extensions.stream().flatMap(v -> v.getFunctions().stream()).collect(Collectors.toMap(AbstractFunction::getName, Function.identity(), (p, n) -> n));
        // the default context is immutable
        context = new ImmutableContext(new MapContext(vars, filters, functions));
        this.traverser = traverser;
    }

    public Evaluator(){
        this(Collections.emptyList(), NodeTraverser.EMPTY);
    }

    /**
     * Parse the source code and generate the expression instance.
     * @param source source code
     * @return ast node
     */
    public Expression build(String source){
        Node ast = parse(source);
        return new Expression(source.getBytes(), ast);
    }

    /**
     * Parse the source code and generate the expression instance.
     * @param source source code
     * @return expression
     */
    public Expression build(byte[] source){
        Node ast = parse(source);
        return new Expression(source, ast);
    }

    /**
     * Parse the source code and generate the ast node.
     * @param source source code
     * @return ast
     */
    public Node parse(String source){
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.lex());
        Node node = parser.parse();
        return this.traverser.traverse(node);
    }

    /**
     * Parse the source code and generate the ast node.
     * @param source source code
     * @return ast
     */
    public Node parse(byte[] source){
        Lexer lexer = new Lexer(source);
        Parser parser = new Parser(lexer.lex());
        Node node = parser.parse();
        return this.traverser.traverse(node);
    }

    /**
     * Evaluate the expression.
     * @param expression the expression
     * @param context runtime context
     * @return the result
     */
    public Object evaluate(Expression expression, Context context){
        return expression.getAst().evaluate(new DelegatingContext(Arrays.asList(context, this.context)));
    }

    /**
     * Evaluate the expression.
     * @param expression the expression
     * @return the result
     */
    public Object evaluate(Expression expression){
        return expression.getAst().evaluate(this.context);
    }

    /**
     * Evaluate the expression source code.
     * @param source source code
     * @param context runtime context
     * @return the result
     */
    public Object evaluate(String source, Context context){
        return parse(source).evaluate(new DelegatingContext(Arrays.asList(context, this.context)));
    }

    /**
     * Evaluate the expression source code.
     * @param source source code
     * @return the result
     */
    public Object evaluate(String source){
        return parse(source).evaluate(this.context);
    }
}

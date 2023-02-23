package io.github.slince.expression;

public class EvaluationException extends RuntimeException {

    public EvaluationException(String message){
        super(message);
    }

    public EvaluationException(Throwable e){
        super(e);
    }
}

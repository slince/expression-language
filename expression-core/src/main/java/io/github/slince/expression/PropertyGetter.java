package io.github.slince.expression;

public interface PropertyGetter {

     /**
      * Returns the specified property value
      * @param name the property name
      * @return the property value
      */
     Object getProperty(String name);
}

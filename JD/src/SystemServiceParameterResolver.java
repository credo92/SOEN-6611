package metricTests;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import ast.SystemObject;

public  class SystemServiceParameterResolver implements ParameterResolver {
 
  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return new SystemObject();
  }
 
  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return (parameterContext.getParameter().getType() == SystemObject.class);
  }
 
}
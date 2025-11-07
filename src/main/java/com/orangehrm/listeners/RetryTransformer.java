package com.orangehrm.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Version-safe Retry transformer that does NOT reference
 * getRetryAnalyzer()/getRetryAnalyzerClass() at compile time.
 */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {

        Class<?> retryClazz = RetryAnalyzer.class;

        // Try new API: getRetryAnalyzer()
        if (invokeGetAndMaybeSet(annotation, "getRetryAnalyzer", retryClazz)) {
            return;
        }

        // Fallback to old API: getRetryAnalyzerClass()
        invokeGetAndMaybeSet(annotation, "getRetryAnalyzerClass", retryClazz);
    }

    /**
     * Tries to call a getter by name (either getRetryAnalyzer or getRetryAnalyzerClass).
     * If it returns null, calls setRetryAnalyzer(Class) reflectively.
     *
     * @return true if the method existed & was handled, false if the method didn't exist.
     */
    private boolean invokeGetAndMaybeSet(ITestAnnotation annotation, String getterName, Class<?> retryClazz) {
        try {
            Method getter = annotation.getClass().getMethod(getterName);
            Object existing = getter.invoke(annotation);
            if (existing == null) {
                // Both old and new TestNGs expose setRetryAnalyzer(Class)
                Method setter = annotation.getClass().getMethod("setRetryAnalyzer", Class.class);
                setter.invoke(annotation, retryClazz);
            }
            return true; // getter existed; we’re done
        } catch (NoSuchMethodException e) {
            // This TestNG version doesn't have that getter; try the other one.
            return false;
        } catch (Exception e) {
            System.err.println("RetryTransformer: unable to set retry analyzer via " + getterName + " -> " + e);
            return true; // method existed but failed; stop further attempts
        }
    }
}

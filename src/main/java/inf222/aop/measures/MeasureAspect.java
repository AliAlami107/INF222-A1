package inf222.aop.measures;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
public class MeasureAspect {
    private final String regex;
    private final Pattern pattern;

    private final Map<String, Double> toMeter = new HashMap<String, Double>(Map.of(
            "m", 1d,
            "ft", 0.3048d,
            "in", 0.0254d,
            "cm", 0.01d,
            "yd", 0.9144d));

    public MeasureAspect() {
        String elems = String.join("|", toMeter.keySet());
        regex = String.format(".*_(%s)$", elems);
        pattern = Pattern.compile(regex);
    }

    @Around("get(double inf222.aop.measures.Measures.*)")
    public Object convertToMeters(ProceedingJoinPoint jp) throws Throwable {
        // Get the field name from the join point signature
        String fieldName = jp.getSignature().getName();
        
        // Check if the field name matches our pattern
        Matcher matcher = pattern.matcher(fieldName);
        if (matcher.matches()) {
            // Extract the measure suffix (e.g., "ft", "cm", etc.)
            String measure = matcher.group(1);
            
            // Get the conversion factor to meters
            Double conversionFactor = toMeter.get(measure);
            
            if (conversionFactor != null) {
                // Proceed to get the actual stored value
                Double value = (Double) jp.proceed();
                
                // Convert to meters and return
                return value * conversionFactor;
            }
        }
        
        // If no match, proceed normally
        return jp.proceed();
    }

    @Around("set(* *) && target(inf222.aop.measures.Measures) && !cflow(execution(inf222.aop.measures.Measures.new(..)))")
    public Object convertFromMeters(ProceedingJoinPoint jp) throws Throwable {
        // Get the field name from the join point signature
        String fieldName = jp.getSignature().getName();
        
        // Get the new value being set (first argument)
        Object[] args = jp.getArgs();
        if (args.length == 0 || !(args[0] instanceof Double)) {
            return jp.proceed();
        }
        Double newValue = (Double) args[0];
        
        // Part 1.3: Check if the value is negative
        if (newValue < 0) {
            throw new Error("Illegal modification");
        }
        
        // Check if the field name matches our pattern
        Matcher matcher = pattern.matcher(fieldName);
        if (matcher.matches()) {
            // Extract the measure suffix (e.g., "ft", "cm", etc.)
            String measure = matcher.group(1);
            
            // Get the conversion factor to meters
            Double conversionFactor = toMeter.get(measure);
            
            if (conversionFactor != null && conversionFactor != 0) {
                // Convert from meters back to the original measure
                // The value coming in is in meters (because it was computed using converted values)
                // So we need to divide by the conversion factor to get back to the original measure
                Double convertedValue = newValue / conversionFactor;
                
                // Proceed with the converted value
                return jp.proceed(new Object[] {convertedValue});
            }
        }
        
        // If no match, proceed normally
        return jp.proceed();
    }

}

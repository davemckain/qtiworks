/* $Id$
 *
 * Copyright 2011 University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.jqtiplus.node.expression.operator.math;


/**
 * @author  David McKain
 * @version $Revision$
 */
public class StatsFunctions {
    
    public static double mean(double[] values) {
        if (values.length==0) {
            return Double.NaN;
        }
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }
    
    public static double sumSquaresOfDifferencesFromMean(double[] values) {
        if (values.length==0) {
            return Double.NaN;
        }
        double mean = mean(values);
        double sum = 0;
        for (double value : values) {
            double diff = (value - mean);
            sum += diff*diff;
        }
        return sum;
    }
    
    public static double populationVariance(double[] values) {
        if (values.length==0) {
            return Double.NaN;
        }
        return sumSquaresOfDifferencesFromMean(values) / values.length;
    }
    
    public static double sampleVariance(double[] values) {
        if (values.length<2) {
            return Double.NaN;
        }
        return sumSquaresOfDifferencesFromMean(values) / (values.length - 1);
    }
    
    public static double populationSD(double[] values) {
        return Math.sqrt(populationVariance(values));
    }
    
    public static double sampleSD(double[] values) {
        return Math.sqrt(sampleVariance(values));
    }

}

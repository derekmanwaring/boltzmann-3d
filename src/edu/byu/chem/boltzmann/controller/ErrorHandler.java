/*
 * Boltzmann 3D, a kinetic theory demonstrator
 * Copyright (C) 2013 Dr. Randall B. Shirts
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.byu.chem.boltzmann.controller;

import edu.byu.chem.boltzmann.model.physics.Physics;
import edu.byu.chem.boltzmann.utils.data.SimulationInfo;
import java.awt.EventQueue;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ErrorHandler {

    //Seconds until cumulative statistics are reset (forgetMultiplier * collisionLifetime)
    public double forgetMultiplier = 4.0;

    public SimulationInfo simulationInfo;
    
    public Physics getPhysics() {
        return null;
    }
    
    public static final UncaughtExceptionHandler EXCEPTION_HANDLER = new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, final Throwable t) {
            
            /**
             * Make a new thread if this is the event dispatching thread.
             */
            if (EventQueue.isDispatchThread()) {
                new Thread("Exception Notifier") {
                    @Override
                    public void run() {
                        errorMessage(t);
                        System.exit(-1);
                    }
                }.start();
            } else {
                errorMessage(t);
                System.exit(-1);
            }
        }
    };
    
    private static class AWTExceptionHandler {
        public void handle(final Throwable t) {
            EXCEPTION_HANDLER.uncaughtException(null, t);
        }
    }
    
    public static void attachDefaultExceptionHandlers() {        
        Thread.setDefaultUncaughtExceptionHandler(EXCEPTION_HANDLER);        
        System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());
    }
    
    public static void errorMessage(final Throwable error) {  
        try {      
            error.printStackTrace(System.err);
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(null, "The following error has occured:\n" + error,
                            "Boltzmann 3D", JOptionPane.ERROR_MESSAGE);
                }
            });       
        } catch (Exception e) {
            System.err.println("Could not wait for user to read error message.");
            e.printStackTrace(System.err);
        }
    }

    public static void infoMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, message, "Boltzmann 3D", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    protected static void log(String message, StackTraceElement[] stackTrace) {
        System.out.println(message);
        System.out.println(Arrays.toString(stackTrace));
    }

    /**
     * Utility method to format a double precision number to prec
     * significant figures using scientific notation if necessary.
     */
    public static String format(double num, int prec) {
            return format(num, prec, true);
    }

    public static String format(double num, int prec, boolean useDefault) {
        NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setGroupingUsed(false);

        try {
            if(Double.isNaN(num)||Double.isInfinite(num)) {
                    num = 0;
            }
            
            String formatString = "";
            formatString += "0.";
            for(int i = 0; i < prec - 1; i++) {
                formatString += "0";
            }
            
            formatString += "E0";
            DecimalFormat ldf = new DecimalFormat(formatString);
            String sciNot = ldf.format(num);
            
            int exp = numFormat.parse(sciNot.substring(sciNot.indexOf("E") + 1)).intValue();
            if(exp > 3 || exp < -2) {
                return sciNot;
            }
            
            double parsed = numFormat.parse(sciNot).doubleValue();
            int leftDigits=(("" + parsed).replaceAll("-", "")).indexOf(".");
            if(leftDigits >= prec || leftDigits < 0) {
                formatString = "0";
            } else {
                formatString = "0.";
                if(leftDigits == 1 && parsed < 1.0) {
                    formatString += "0";
                }
                for(int i = 0; i < (prec - leftDigits); i++) {
                    formatString += "0";
                }
            }
            
            NumberFormat nf;
            if(useDefault) {
                nf=new DecimalFormat(formatString);
            } else {
                nf=NumberFormat.getNumberInstance(Locale.US);
                nf.setMaximumFractionDigits(formatString.length()-formatString.indexOf(".")-1);
                nf.setMinimumFractionDigits(formatString.length()-formatString.indexOf(".")-1);
                nf.setGroupingUsed(false);
            }
            //System.out.println(nf+"\t"+nf.format(parsed));
            String finalVal=nf.format(parsed);
            return finalVal;
        } catch(NumberFormatException e) {
            return "0";
        } catch(Exception e) {
            //reportError(null, e);
            throw new RuntimeException(null, e);
        }
    }
}

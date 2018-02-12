// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Used to mark fields to be tracked by the FieldMonitor
@Retention(RetentionPolicy.RUNTIME)
public @interface Synchronize {
}

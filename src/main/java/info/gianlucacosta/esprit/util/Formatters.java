package info.gianlucacosta.esprit.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public interface Formatters {
    DecimalFormat longFormatter =
            (DecimalFormat) NumberFormat.getInstance(Locale.FRENCH);
}

package cristo.app.proyectodm.core;

import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.text.DecimalFormat;
import java.util.Locale;

public class SettingsManager {

    private boolean isDarkMode;
    private boolean isMetricSys;
    private String language;
    private Resources resources;

    private double pasoEnKm;
    private double km_millas;
    private static DecimalFormat df;

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void setDarkMode(boolean darkMode) {
        isDarkMode = darkMode;
    }

    public boolean isMetricSys() {
        return isMetricSys;
    }

    public void setMetricSys(boolean metricSys) {
        isMetricSys = metricSys;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String stepsToDistance(int pasos) {
        double distancia = pasos * this.pasoEnKm;
        String units = "km";
        if (!this.isMetricSys) {
            distancia = distancia * this.km_millas;
            units = "mi";
        }
        return df.format(distancia) + " " + units;
    }

    public void applyDarkModeOption() {
        if (this.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void applyLanguageOption() {
        final Configuration configuration = resources.getConfiguration();
        final Locale locale = new Locale(language);
        if (!configuration.locale.equals(locale)) {
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        }
    }

    public void applySettings() {
        this.applyDarkModeOption();
        this.applyLanguageOption();
    }


    public SettingsManager(Resources r) {
        this.resources = r;
        this.pasoEnKm = 0.000762;
        this.km_millas = 0.6214;
        this.df = new DecimalFormat("0.00");
    }
}

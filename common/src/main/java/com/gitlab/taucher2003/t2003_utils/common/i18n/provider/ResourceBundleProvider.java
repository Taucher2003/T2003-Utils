package com.gitlab.taucher2003.t2003_utils.common.i18n.provider;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleProvider implements LocaleBundleProvider {

    private final String bundleName;

    public ResourceBundleProvider(String bundleName) {
        this.bundleName = bundleName;
    }

    @Override
    public LocaleBundle getBundle(Locale locale) {
        return new ResourceBundleBundle(ResourceBundle.getBundle(bundleName, locale, DefaultLocalizerControl.SINGLETON));
    }

    @Override
    public String toString() {
        return "ResourceBundleProvider{" +
                "bundleName='" + bundleName + '\'' +
                '}';
    }

    private static final class ResourceBundleBundle implements LocaleBundle {

        private final ResourceBundle resourceBundle;

        private ResourceBundleBundle(ResourceBundle resourceBundle) {
            this.resourceBundle = resourceBundle;
        }

        @Override
        public String getString(String key) {
            return resourceBundle.getString(key);
        }

        @Override
        public boolean containsKey(String key) {
            return resourceBundle.containsKey(key);
        }
    }

    private static final class DefaultLocalizerControl extends ResourceBundle.Control {

        private static final DefaultLocalizerControl SINGLETON = new DefaultLocalizerControl();

        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return Locale.ROOT;
        }
    }
}

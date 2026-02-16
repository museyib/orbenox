import {createI18n} from "vue-i18n";

const loaders = {
    en: () => import('./locales/en.json'),
    az: () => import('./locales/az.json'),
}

const fallbackLocale = 'en'

function resolveStartLocale() {
    const saved = localStorage.getItem('locale');
    if (saved && loaders[saved])
        return saved;
    const browser = (navigator.language || 'en').slice(0, 2);
    return loaders[browser] ? browser : fallbackLocale;
}

const i18n = createI18n({
    legacy: false,
    locale: resolveStartLocale(),
    fallbackLocale,
    messages: {},
    missingWarn: false,
    fallbackWarn: false
});

export async function ensureLocale(locale) {
    if (!i18n.global.availableLocales.includes(locale)) {
        const mod = await loaders[locale]();
        i18n.global.setLocaleMessage(locale, mod.default || mod);
    }
    i18n.global.locale.value = locale;
    localStorage.setItem('locale', locale);
}

ensureLocale(i18n.global.locale.value);
export default i18n;
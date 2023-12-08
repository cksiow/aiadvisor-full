import i18n from 'i18next';
import {initReactI18next} from 'react-i18next';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as RNLocalize from 'react-native-localize';
import en from './en.json';
import zh from './zh.json';
import ms from './ms.json';
import ar from './ar.json';
import de from './de.json';
import fr from './fr.json';
import ga from './ga.json';
import it from './it.json';
import no from './no.json';
import ro from './ro.json';
import ta from './ta.json';
import sv from './sv.json';
import es from './es.json';
import th from './th.json';
import pt from './pt.json';
import id from './id.json';
import vi from './vi.json';
import ja from './ja.json';
import ko from './ko.json';
import fil from './fil.json';
import nl from './nl.json';
const LANGUAGES = {
  en,
  zh,
  ms,
  ar,
  de,
  fr,
  ga,
  it,
  no,
  ro,
  sv,
  ta,
  es,
  th,
  pt,
  id,
  vi,
  ja,
  ko,
  fil,
  nl,
};

const LANG_CODES = Object.keys(LANGUAGES);

const LANGUAGE_DETECTOR = {
  type: 'languageDetector',
  async: true,
  detect: async callback => {
    await AsyncStorage.getItem('user-language', (err, language) => {
      // if error fetching stored data or no language was stored
      // display errors when in DEV mode as console statements
      if (err || !language) {
        if (err) {
          console.log('Error fetching Languages from asyncstorage ', err);
        } else {
          console.log('No language is set, choosing English as fallback');
        }
        const findBestAvailableLanguage =
          RNLocalize.findBestLanguageTag(LANG_CODES);
        callback(
          findBestAvailableLanguage == null
            ? 'en'
            : findBestAvailableLanguage.languageTag || 'en',
        );
        return;
      }
      callback(language);
    });
  },
  init: () => {},
  cacheUserLanguage: language => {
    //in here will store the selected language
    AsyncStorage.setItem('user-language', language);
  },
};

i18n
  .use(LANGUAGE_DETECTOR)
  .use(initReactI18next)
  .init({
    compatibilityJSON: 'v3',
    resources: LANGUAGES,
    react: {
      useSuspense: false,
    },
    interpolation: {
      escapeValue: false,
    },
    fallbackLng: 'en',
  });

export default i18n;

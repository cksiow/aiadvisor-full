/**
 * @format
 */
import 'react-native-gesture-handler'; // on top
import {AppRegistry} from 'react-native';
import App from './App';
import {name as appName} from './app.json';
import analytics from '@react-native-firebase/analytics';

if (__DEV__) {
  console.log('Disable AnalyticsCollection due to debug mode');
  // Disable analytics in debug mode
  analytics().setAnalyticsCollectionEnabled(false);
} else {
  console.log('Enable AnalyticsCollection due to production');
  analytics().setAnalyticsCollectionEnabled(true);
}

AppRegistry.registerComponent(appName, () => App);

/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, {useEffect, useState} from 'react';
import {StyleSheet} from 'react-native';
import API from './config/Api';

import DeviceInfo from 'react-native-device-info';

import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {createDrawerNavigator} from '@react-navigation/drawer';
import {NavigationContainer} from '@react-navigation/native';

import AdvisorScreen from './screen/AdvisorScreen';
import HistoryScreen from './screen/HistoryScreen';
import AssistantsScreen from './screen/AssistantsScreen';
import InAppPurchaseScreen from './screen/InAppPurchaseScreen';
import Icon6 from 'react-native-vector-icons/FontAwesome6';
import Icon from 'react-native-vector-icons/FontAwesome';
import IconM from 'react-native-vector-icons/MaterialIcons';
import Device from './utils/Device';
import './translate/i18n';
const BottomTab = createBottomTabNavigator();
const Drawer = createDrawerNavigator();

import {useTranslation} from 'react-i18next';
import LanguageScreen from './screen/LanguageScreen';
import mobileAds from 'react-native-google-mobile-ads';
import SpInAppUpdates, {
  IAUUpdateKind,
  StartUpdateOptions,
} from 'sp-react-native-in-app-updates';
import AdsUtil from './utils/Ads';

import AsyncStorage from '@react-native-async-storage/async-storage';
import LoadingScreen from './screen/LoadingScreen';
const inAppUpdates = new SpInAppUpdates(false);
import {
  BackHandler,
  View,
  ActivityIndicator,
  ImageBackground,
} from 'react-native';

import analytics from '@react-native-firebase/analytics';
export const navigationRef = React.createRef();

function App(): JSX.Element {
  const {t} = useTranslation();
  //reason why got loading screen as 1st screen because if AdvisorScreen in first screen the componentDidMount will called 4 times
  // and only last time will success
  const MainStack = () => (
    <BottomTab.Navigator>
      <BottomTab.Screen
        name="Loading"
        component={LoadingScreen}
        initialParams={{initialScreen: true}}
        options={{
          headerShown: false,
          tabBarIcon: ({focused, color, size}) => {
            return (
              <IconM
                name="money"
                size={1}
                color={focused ? 'green' : 'white'}
              />
            );
          },
          tabBarStyle: {
            backgroundColor: 'black',
            display: 'none',
          },
          tabBarItemStyle: {
            backgroundColor: 'black',
            display: 'none',
          },
        }}
      />
      <BottomTab.Screen
        name="Advisor"
        component={AdvisorScreen}
        options={{
          headerShown: false,
          tabBarLabel: t('menu.advisor'),
          tabBarIcon: ({focused, color, size}) => {
            return (
              <IconM
                name="camera"
                size={35}
                color={focused ? '#58C936' : 'white'}
              />
            );
          },
          tabBarStyle: {
            backgroundColor: 'black',
          },
        }}
      />
      <BottomTab.Screen
        name="History"
        component={HistoryScreen}
        options={{
          headerShown: false,
          tabBarLabel: t('menu.history'),
          tabBarIcon: ({focused, color, size}) => {
            return (
              <Icon
                name="history"
                size={25}
                color={focused ? '#58C936' : 'white'}
              />
            );
          },
          tabBarStyle: {
            backgroundColor: 'black',
          },
        }}
      />
      <BottomTab.Screen
        name="Assistants"
        component={AssistantsScreen}
        options={{
          headerShown: false,
          tabBarLabel: t('menu.assistants'),
          tabBarIcon: ({focused, color, size}) => {
            return (
              <Icon6
                name="user-astronaut"
                size={25}
                color={focused ? '#58C936' : 'white'}
              />
            );
          },
          tabBarStyle: {
            backgroundColor: 'black',
          },
        }}
      />
      <BottomTab.Screen
        name="Language"
        component={LanguageScreen}
        options={{
          headerShown: false,
          tabBarLabel: t('menu.language'),
          tabBarIcon: ({focused, color, size}) => {
            return (
              <Icon6
                name="language"
                size={25}
                color={focused ? '#58C936' : 'white'}
              />
            );
          },
          tabBarStyle: {
            backgroundColor: 'black',
          },
        }}
      />
    </BottomTab.Navigator>
  );

  const InAppPurhaseStack = () => (
    <BottomTab.Navigator>
      <BottomTab.Screen
        name="Top up credit"
        component={InAppPurchaseScreen}
        options={{
          headerShown: false,
          tabBarIcon: ({focused, color, size}) => {
            return (
              <IconM
                name="money"
                size={35}
                color={focused ? 'green' : 'white'}
              />
            );
          },
          tabBarStyle: {
            backgroundColor: 'black',
            display: 'none',
          },
        }}
      />
    </BottomTab.Navigator>
  );

  const handleBackButton = () => {
    //navigate to Advisor instead of Loading
    navigationRef.current.navigate('Advisor');
    return true;
  };

  const [hasRunEffect, setHasRunEffect] = useState(false);
  const [isLoading, setLoading] = useState(true);
  useEffect(() => {
    if (!hasRunEffect) {
      // Set the state to prevent future executions
      setHasRunEffect(true);
      BackHandler.addEventListener('hardwareBackPress', handleBackButton);
      const fetchUniqueId = async () => {
        try {
          if (!__DEV__) {
            await API.setServerUrl();
          }

          setLoading(false);
          //get and set uniqueId into local
          //create user record based on uniqueId
          const apiUrl = API.getAPIUrl() + '/user';
          var languageCode = null;
          if (Device.getUniqueId() == null) {
            const id = await DeviceInfo.getUniqueId();
            languageCode = await AsyncStorage.getItem('user-language');
            Device.setUniqueId(id);
          }
          if (languageCode == null || languageCode == '') {
            languageCode = 'en';
          }
          const options = {
            method: 'POST',
            body: JSON.stringify({
              uniqueId: Device.getUniqueId(),
              buildNumber: DeviceInfo.getBuildNumber(),
              languageCode: languageCode,
            }),
            headers: {
              'Content-Type': 'application/json',
            },
          };

          fetch(apiUrl, options)
            .then(res => res.json())
            .then(
              result => {},
              error => {},
            );
        } catch (error) {
          console.error('Error save user:', error);
        }
      };
      //get server ip

      fetchUniqueId();
      //do others initial after 2 seconds to make screen load faster
      setTimeout(async () => {
        console.log('Load mobileAds');
        //set test device
        await mobileAds().setRequestConfiguration({
          //xiaomi 13 pro release,production,debug,emulator
          testDeviceIdentifiers: [
            '88018F4D5A1C611E5DD0B205CCC35629',
            'C2A9D8800CA59DE7EFB5B1C5479261D4',
            'C99E50F30ABF289098FF84F469878909',
            'EBFE12E68336EC9E7BD36119CEDDE179',
          ],
        });
        mobileAds()
          .initialize()
          .then(async adapterStatuses => {
            console.log('Load RewardVideo');
            //load reward video
            AdsUtil.startLoad();
          });
        if (!__DEV__) {
          console.log('Check version update');
          inAppUpdates
            .checkNeedsUpdate({curVersion: DeviceInfo.getBuildNumber()})
            .then(result => {
              console.log(result);
              console.log(result.storeVersion);
              if (result.shouldUpdate) {
                analytics().logEvent('version_upgrade_request', {
                  currentVersion: DeviceInfo.getBuildNumber(),
                });
                let updateOptions: StartUpdateOptions = {};
                updateOptions = {
                  updateType: IAUUpdateKind.FLEXIBLE,
                };

                inAppUpdates.startUpdate(updateOptions);
              }
            });
        }
      }, 2000);
    }
  }, [hasRunEffect]);

  return (
    <NavigationContainer ref={navigationRef}>
      {isLoading && (
        <View style={styles.container}>
          <ImageBackground
            source={require('./assets/loading.jpg')}
            style={styles.image}>
            <ActivityIndicator size={20} color="#fff" />
          </ImageBackground>
        </View>
      )}
      {!isLoading && (
        <Drawer.Navigator
          screenOptions={{
            drawerStyle: {
              backgroundColor: '#c6cbef',
              width: 240,
            },
            headerShown: false,
          }}>
          <Drawer.Screen name="MainScreen" component={MainStack} />
          <Drawer.Screen
            name="InAppPurchaseScreen"
            component={InAppPurhaseStack}
          />
        </Drawer.Navigator>
      )}
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'black',
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
  image: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default App;

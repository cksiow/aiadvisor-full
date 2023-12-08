import {React} from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  SafeAreaView,
} from 'react-native';
import {DataBindingComponent} from 'react-native-twowaybinding';
import API from '../config/Api';
import Device from '../utils/Device';
import DeviceInfo from 'react-native-device-info';
import ErrorUtil from '../utils/Error';
import AdsUtil from '../utils/Ads';
import Toast, {BaseToast} from 'react-native-toast-message';
import {
  initConnection,
  requestPurchase,
  getProducts,
  finishTransaction,
  flushFailedPurchasesCachedAsPendingAndroid,
} from 'react-native-iap';
import {withTranslation} from 'react-i18next';

import {RewardedAdEventType, AdEventType} from 'react-native-google-mobile-ads';

class InAppPurchaseScreen extends DataBindingComponent {
  constructor(props) {
    super(props);
    const {t} = props;
    this.state = {
      products: [
        {
          productId: 'rewarded',
          name: t('iap.free'),
          price: '',
          description: t('iap.freeLoadingDescription'),
        },
      ],
      freeLoading: true,
    };
  }
  randomString = () =>
    [...Array(10)].map(() => Math.random().toString(36)[2]).join('');

  async componentDidMount() {
    initConnection()
      .then(async () => {
        //must add random string, otherwise will never refresh
        const products = await getProducts({
          skus: [
            '100_credits',
            '300_credits',
            '500_credits',
            '1000_credits',
            '3000_credits',
            '5000_credits',
            this.randomString(),
          ],
        });

        products.sort(
          (a, b) =>
            a.oneTimePurchaseOfferDetails.priceAmountMicros -
            b.oneTimePurchaseOfferDetails.priceAmountMicros,
        );
        //flush
        await flushFailedPurchasesCachedAsPendingAndroid();
        this.setState(prevState => ({
          products: [...prevState.products, ...products],
        }));
      })
      .finally(async () => {});

    this.unsubscribeLoaded = AdsUtil.getRewardedAd().addAdEventListener(
      RewardedAdEventType.LOADED,
      async () => {
        //enable reward
        console.log('Rewarded video ready to show after screen load');
        await this.insertRewardDisplay();
      },
    );
    this.unsubscribeEarned = AdsUtil.getRewardedAd().addAdEventListener(
      RewardedAdEventType.EARNED_REWARD,
      async reward => {
        const {t} = this.props;
        //set back to loading
        this.state.products.filter(
          s => s.productId == 'rewarded',
        )[0].description = t('iap.freeLoadingDescription');
        await this.setState({
          freeLoading: true,
        });
        //increase credit based on unique id
        const options = {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
        };
        fetch(
          API.getAPIUrl() + `/user/rewarded?uniqueId=${Device.getUniqueId()}`,
          options,
        )
          .then(async response => {
            if (response.status == 400) {
              var json = await response.json();
              throw new Error(json.errorMessage);
            } else {
              return response;
            }
          })
          .then(async responseData => {
            //show message
            Toast.show({
              text1: t('iap.earn'),
              text2: t('iap.earnDescription'),
            });
          })
          .catch(async error => {})
          .finally(async () => {});
      },
    );

    this.unsubscribeClosed = AdsUtil.getRewardedAd().addAdEventListener(
      AdEventType.CLOSED,
      async closed => {
        const {t} = this.props;
        this.state.products.filter(
          s => s.productId == 'rewarded',
        )[0].description = t('iap.freeLoadingDescription');
        await this.setState({
          freeLoading: true,
        });
        //load new rewarded video
        AdsUtil.startLoad();
      },
    );
    //determine is the rewarded ads loaded then insert display
    //function loaded() got issue, need custom
    /*
        public isLoaded = () =>{
          return this._loaded;
        }
    */

    if (AdsUtil.getRewardedAd().isLoaded()) {
      console.log('Rewarded video ready to show while it is loaded early');
      await this.insertRewardDisplay();
    }
  }
  async insertRewardDisplay() {
    const {t} = this.props;
    this.state.products.filter(s => s.productId == 'rewarded')[0].description =
      t('iap.freeDescription');
    await this.setState({freeLoading: false});
  }

  componentWillUnmount() {
    this.unsubscribeLoaded();
    this.unsubscribeEarned();
    this.unsubscribeClosed();
  }

  purchaseCredit = async productId => {
    if (productId == 'rewarded') {
      //watch reward video
      AdsUtil.getRewardedAd().show();
    } else {
      if (Device.getUniqueId() == null) {
        const id = await DeviceInfo.getUniqueId();
        Device.setUniqueId(id);
      }

      initConnection().then(async () => {
        var purchase = await requestPurchase({
          skus: [productId],
        });
        //only success then finishTransaction
        if (purchase[0].purchaseStateAndroid == 1) {
          var finish = await finishTransaction({
            purchase: purchase[0],
            isConsumable: true,
            developerPayloadAndroid: '',
          });
          if (finish.code == 'OK') {
            //after finished call API to store the purchase data + increase the credit
            //call API
            const options = {
              method: 'POST',
              body: JSON.stringify({
                token: finish.purchaseToken,
                productId: purchase[0].productIds[0],
                uniqueId: Device.getUniqueId(),
                quality: JSON.parse(purchase[0].dataAndroid).quantity,
              }),
              headers: {
                'Content-Type': 'application/json',
              },
            };

            fetch(API.getAPIUrl() + '/iap/token', options)
              .then(async response => {
                if (response.status == 400) {
                  var json = await response.json();
                  throw new Error(json.errorMessage);
                } else {
                  return response;
                }
              })
              .catch(error => {
                ErrorUtil.handleError('Send message:', error, this);
              })
              .finally(() => {
                this.props.navigation.goBack();
              });
          }
        }
      });
    }
  };

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <View style={{padding: 5, width: '100%', flexGrow: 1}}>
            <FlatList
              data={this.state.products}
              keyExtractor={(item, index) => index}
              renderItem={({item, index}) => (
                <View
                  style={{
                    margin: 5,
                    borderWidth: 3,
                    borderColor: 'white',
                    padding: 5,
                    borderRadius: 10,
                  }}>
                  <TouchableOpacity
                    onPress={() => {
                      this.purchaseCredit(item.productId);
                    }}
                    disabled={
                      item.productId == 'rewarded' && this.state.freeLoading
                    }
                    style={{}}>
                    <Text
                      style={{
                        alignSelf: 'center',
                        backgroundColor: 'transparent',
                        color: 'white',
                        fontSize: 20,
                        fontWeight: 'bold',
                      }}>{`${item.name} ${item.price}`}</Text>
                    <Text
                      style={{
                        width: '100%',
                        alignSelf: 'center',
                        backgroundColor:
                          item.productId == 'rewarded' && this.state.freeLoading
                            ? '#454545'
                            : 'gold',
                        color:
                          item.productId == 'rewarded' && this.state.freeLoading
                            ? 'white'
                            : 'black',
                        fontSize: 20,
                        fontWeight: 'bold',
                      }}>{`${item.description}`}</Text>
                  </TouchableOpacity>
                </View>
              )}
            />
          </View>
        </View>
        <Toast
          config={toastConfig}
          type="success"
          position="top"
          visibilityTime={10000}
          autoHide={true}
        />
      </SafeAreaView>
    );
  }
}
const toastConfig = {
  /*
    Overwrite 'success' type,
    by modifying the existing `BaseToast` component
  */
  success: props => (
    <BaseToast
      {...props}
      contentContainerStyle={{padding: 12}}
      style={{borderLeftColor: 'lightgreen'}}
      text1Style={{
        fontSize: 16,
        fontWeight: 'bold',
        color: 'black',
      }}
      text2Style={{
        fontSize: 12,
        fontWeight: 'bold',
        color: 'black',
      }}
      text2NumberOfLines={2}
    />
  ),
};
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'black',
  },
});

export default withTranslation()(InAppPurchaseScreen);

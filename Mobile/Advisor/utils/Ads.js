import {RewardedAd} from 'react-native-google-mobile-ads';
const adUnitId = 'ca-app-pub-8509285697310974/8557315745';
export default class Ads {
  static rewarded = RewardedAd.createForAdRequest(adUnitId, {
    requestNonPersonalizedAdsOnly: true,
  });
  constructor() {}

  static startLoad() {
    Ads.rewarded.load();
  }

  static getRewardedAd() {
    return Ads.rewarded;
  }
}

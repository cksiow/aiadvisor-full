import FetchUtil from '../utils/Fetch';
export default class Api {
  constructor() {}
  static serverUrl = 'http://cksiow84.ddns.net';
  static apiInfo = {
    get domain() {
      return __DEV__ ? 'http://192.168.0.109:8080' : Api.serverUrl;
    }, //"http://luckylottery.ddns.net",//"http://192.168.0.109:8080"
    get url() {
      return this.domain + '/aiadvisor';
    },
  };

  static getAPIUrl() {
    return this.apiInfo.url;
  }

  static getAPIDomain() {
    return this.apiInfo.domain;
  }

  static async setServerUrl() {
    await FetchUtil.fetchWithTimeout(
      'http://ip.klse2u.veryfast.biz.user.fm/aiadvisor-server-url.txt?ts=' +
        Date.now(),
      2000,
    )
      .then(async response => {
        if (response.status == 200) {
          return response.text();
        } else {
          return null;
        }
      })
      .then(async responseData => {
        if (responseData != null) {
          Api.serverUrl = responseData;
        }
      })
      .catch(e => {
        console.warn(e);
      });
  }
}

import _ from 'lodash';
import { ResponsiveComponent } from "react-native-responsive-ui";
import { AppState } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';


import PropTypes from 'prop-types';
export class DataBindingComponent extends ResponsiveComponent {
  constructor(props) {
    super(props);
    const changeListener = AppState.addEventListener('change', (nextAppState) => {
      if (this._backgroundState(nextAppState)) {
        //background
      } else if (this._backgroundState(this.state.appState) && (nextAppState === 'active')) {
        this.onForeground();
      }
      this.setState({ appState: nextAppState });
      if(changeListener != null){
        changeListener.remove();
      }

    });
    this.state = {
      appState: AppState.currentState,
    };
    this.routeName = props.route.name != null ? props.route.name : this.props.parent != null ? this.props.parent.routeName : this.props.navigation.state.routeName;
  }

  static propTypes = {
    onForeground: PropTypes.func
  };

  static defaultProps = {
    onForeground: (v) => v || null
  };


  _backgroundState(state) {
    return state != null && state.match(/inactive|background/);
  }

  onForeground() {

  }

  onBackground() {

  }

  dataBind(k, v) {
    if (v !== undefined) {
      var value = this.state;
      _.set(value, k, v);
      this.setState(value);
    }
    const returnValue = _.get(this.state, k);
    return returnValue;
  }

  dataBindArray(k, v, index) {
    return this.dataBind(k.replace('index', index), v);
  }

  async setUrlStorage(url, data) {
    await AsyncStorage.setItem(url + "#" + this.routeName, JSON.stringify(data))
  }


  async setValueStorage(valuePath, value) {
    AsyncStorage.setItem(this.routeName + "." + valuePath, value.toString())
  }

  async getUrlStorage(url) {
    return await AsyncStorage.getItem(url + "#" + this.routeName)
  }


  async getValueStorage(valuePath) {
    return await AsyncStorage.getItem(this.routeName + "." + valuePath)
  }

}

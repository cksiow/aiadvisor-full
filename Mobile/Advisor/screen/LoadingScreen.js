import {React} from 'react';
import {View, StyleSheet, SafeAreaView, ActivityIndicator} from 'react-native';
import {DataBindingComponent} from 'react-native-twowaybinding';
import {withTranslation} from 'react-i18next';
class LoadingScreen extends DataBindingComponent {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.unsubscribeFocus = this.props.navigation.addListener(
      'focus',
      async () => {
        //navigate to Advisor, MUST wait at least 1 miliseconds, otherwise the Advisor will initial few times
        setTimeout(() => {
          const {navigation} = this.props;
          navigation.navigate('Advisor', {});
        }, 1);
      },
    );
  }

  componentWillUnmount() {
    this.unsubscribeFocus();
  }

  render() {
    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <ActivityIndicator size={20} color="#fff" />
        </View>
      </SafeAreaView>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'black',
  },
});

export default withTranslation()(LoadingScreen);

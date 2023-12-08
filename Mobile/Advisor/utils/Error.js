import {View, Text} from 'react-native';
import i18n from '../translate/i18n'; // Import the i18n instance

export default class Error {
  static handleError(message, error, form) {
    //write out the error
    console.error(message, error);
    //put the error the errorMessage state and show
    form.setState({
      errorMessage: error.message,
      showError: true,
    });
  }

  static generateErrorView(form) {
    return (
      <View style={{flexDirection: 'column', alignItems: 'center'}}>
        <Text
          style={{
            display: form.state.showError ? 'flex' : 'none',
            color: '#E83020',
            fontSize: 16,
            fontWeight: 'bold',
          }}
          onPress={async () => {
            await form.setState({showError: false});
          }}>
          {form.state.errorMessage}
        </Text>
        <Text
          style={{
            display: form.state.showError ? 'flex' : 'none',
            color: 'gold',
            fontSize: 16,
            fontWeight: 'bold',
            textDecorationLine: 'underline',
          }}
          onPress={() => {
            form.props.navigation.navigate('InAppPurchaseScreen');
          }}>
          {i18n.t('askMore')}
        </Text>
      </View>
    );
  }
}

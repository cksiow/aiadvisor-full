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
import {withTranslation} from 'react-i18next';
import HistoryScreen from './HistoryScreen';
class LanguageScreen extends DataBindingComponent {
  constructor(props) {
    super(props);
    this.state = {
      languages: [
        {code: 'ar', label: 'Arabic'},
        {code: 'nl', label: 'Dutch'},
        {code: 'en', label: 'English'},
        {code: 'fil', label: 'Filipino'},
        {code: 'fr', label: 'French'},
        {code: 'ga', label: 'Irish'},
        {code: 'id', label: 'Indonesian'},
        {code: 'it', label: 'Italian'},
        {code: 'ja', label: 'Japanese'},
        {code: 'ko', label: 'Korean'},
        {code: 'ms', label: 'Malay'},
        {code: 'no', label: 'Norwegian'},
        {code: 'pt', label: 'Portuguese'},
        {code: 'ro', label: 'Romanian'},
        {code: 'es', label: 'Spanish'},
        {code: 'sv', label: 'Swedish'},
        {code: 'ta', label: 'Tamil'},
        {code: 'th', label: 'Thai'},
        {code: 'vi', label: 'Vietnamese'},
        {code: 'zh', label: '中文 （简体）'},
      ],
    };
  }

  componentDidMount() {}

  componentWillUnmount() {}

  render() {
    //using withTranslation the i18n can get from this.props
    const {i18n} = this.props;

    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <View style={{padding: 5, flexGrow: 1, height: '80%'}}>
            <FlatList
              data={this.state.languages}
              keyExtractor={(item, index) => index}
              renderItem={({item, index}) => (
                <View
                  style={{
                    margin: 5,
                    flexDirection: 'row',
                    alignItems: 'center',
                  }}>
                  <TouchableOpacity
                    style={{margin: 5, flexGrow: 1, maxWidth: '100%'}}
                    onPress={() => {
                      i18n.changeLanguage(item.code);
                      HistoryScreen.needRefresh();
                    }}>
                    <Text
                      style={{
                        backgroundColor:
                          i18n.language == item.code ? 'black' : '#075E54',
                        borderRadius: 8,
                        padding: 6,
                        borderColor:
                          i18n.language == item.code ? '#27A300' : 'white',
                        borderWidth: 1,
                        fontSize: 20,
                        fontWeight: 'bold',
                        color: i18n.language == item.code ? '#27A300' : 'white',
                      }}>
                      {`${item.label}`}
                    </Text>
                  </TouchableOpacity>
                </View>
              )}
            />
          </View>
        </View>
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'black',
  },
  inputContainer: {
    flexDirection: 'row',
    padding: 4,
    alignItems: 'center',
  },

  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: 'white', // Border color
    borderRadius: 8,
    padding: 10,
    color: 'white',
  },
  button: {
    backgroundColor: '#499C30',
    borderRadius: 8,
    marginLeft: 10,
    padding: 10,
  },
});

export default withTranslation()(LanguageScreen);

import {React} from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  SafeAreaView,
} from 'react-native';
import {DataBindingComponent} from 'react-native-twowaybinding';
import API from '../config/Api';
import Device from '../utils/Device';
import DeviceInfo from 'react-native-device-info';
import ErrorUtil from '../utils/Error';
let needRefresh = true;
class HistoryScreen extends DataBindingComponent {
  constructor(props) {
    super(props);
    this.state = {
      threads: [],
      isLoading: true,
    };
  }

  static needRefresh() {
    needRefresh = true;
  }

  async componentDidMount() {
    this.unsubscribeFocus = this.props.navigation.addListener(
      'focus',
      async () => {
        if (needRefresh) {
          await this.initialScreen();
        }
      },
    );
  }

  componentWillUnmount() {
    this.unsubscribeFocus();
  }

  async initialScreen() {
    await this.setState({isLoading: true});
    //set device id
    if (Device.getUniqueId() == null) {
      const id = await DeviceInfo.getUniqueId();
      Device.setUniqueId(id);
    }
    //get all assistants
    fetch(
      API.getAPIUrl() + `/thread/byUniqueId?uniqueId=${Device.getUniqueId()}`,
    )
      .then(async response => {
        if (response.status == 400) {
          var json = await response.json();
          throw new Error(json.errorMessage);
        } else {
          return response.json();
        }
      })
      .then(async responseData => {
        needRefresh = false;
        await this.setState({
          threads: responseData,
        });
      })
      .catch(error => {
        ErrorUtil.handleError('Error fetching data1:', error, this);
      })
      .finally(async () => {
        await this.setState({isLoading: false});
      });
  }

  displayThreadMessage = threadId => {
    const {navigation} = this.props;
    navigation.navigate('Advisor', {
      threadId: threadId,
    });
  };

  render() {
    const {isLoading} = this.state;
    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <FlatList
            style={{}}
            data={this.state.threads}
            keyExtractor={(item, index) => index}
            renderItem={({item, index}) => (
              <View style={{}}>
                <TouchableOpacity
                  style={{margin: 5}}
                  onPress={() => {
                    this.displayThreadMessage(item.threadId);
                  }}>
                  <Text
                    style={{
                      backgroundColor: '#075E54',
                      borderWidth: 1,
                      borderColor: 'white',
                      borderRadius: 8,
                      padding: 6,
                      fontSize: 24,
                      fontWeight: 'bold',
                      color: 'white',
                    }}>{`${item.subject} ...`}</Text>
                </TouchableOpacity>
              </View>
            )}
          />
        </View>
        {isLoading && (
          <View style={[styles.container, {justifyContent: 'flex-start'}]}>
            <ActivityIndicator size={40} color="#fff" />
          </View>
        )}
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'black',
  },
});

export default HistoryScreen;

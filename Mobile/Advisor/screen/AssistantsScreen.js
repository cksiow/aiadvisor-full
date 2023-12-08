import {React} from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  SafeAreaView,
  Alert,
} from 'react-native';
import {
  DataBindingComponent,
  DataBindingTextbox,
} from 'react-native-twowaybinding';
import API from '../config/Api';
import Device from '../utils/Device';
import DeviceInfo from 'react-native-device-info';
import ErrorUtil from '../utils/Error';
import Icon6 from 'react-native-vector-icons/FontAwesome6';
import Screen from '../utils/Screen';
import {withTranslation} from 'react-i18next';

class AdvisorScreen extends DataBindingComponent {
  constructor(props) {
    super(props);
    this.state = {
      keywords: null,
      assistants: [],
      isLoading: true,
    };
  }

  async componentDidMount() {
    await this.initialScreen();
  }

  async initialScreen() {
    console.log('initialScreen');
    await this.setState({isLoading: true});
    //set device id
    if (Device.getUniqueId() == null) {
      const id = await DeviceInfo.getUniqueId();
      Device.setUniqueId(id);
    }
    //get all assistants
    fetch(
      API.getAPIUrl() + `/assistant/personal?uniqueId=${Device.getUniqueId()}`,
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
        await this.setState({assistants: responseData});
      })
      .catch(error => {
        ErrorUtil.handleError('Error fetching data:', error, this);
      })
      .finally(async () => {
        await this.setState({isLoading: false});
      });
  }

  deleteAssistant = item => {
    Alert.alert(
      'Confirmation',
      'Do you want to proceed?',
      [
        {
          text: 'No',
          style: 'cancel',
        },
        {
          text: 'Yes',
          onPress: () => {
            const options = {
              method: 'DELETE',
              body: JSON.stringify({
                assistantId: item.id,
                uniqueId: Device.getUniqueId(),
              }),
              headers: {
                'Content-Type': 'application/json',
              },
            };
            fetch(API.getAPIUrl() + '/assistant/personal', options)
              .then(async response => {
                if (response.status == 400) {
                  var json = await response.json();
                  throw new Error(json.errorMessage);
                } else {
                  return response;
                }
              })
              .then(async responseData => {
                //once done remove results
                await this.setState({
                  isLoading: false,
                  assistants: this.state.assistants.filter(
                    d => d.id !== item.id,
                  ),
                });
                //refresh AdvisorScreen
                await Screen.getAssistantScreen().initialScreen(false);
              })
              .catch(async error => {
                ErrorUtil.handleError('delete assistant:', error, this);
              })
              .finally(async () => {
                await this.setState({isLoading: false});
              });
          },
        },
      ],
      {cancelable: true},
    );
  };

  async saveAssistant() {
    const {t} = this.props;
    if (this.state.keywords == null || this.state.keywords == '') {
      await this.setState({
        showError: true,
        errorMessage: t('assistants.messageValidation1'),
      });
      return;
    }
    //hide error message
    await this.setState({
      showError: false,
      errorMessage: null,
    });
    //save
    const options = {
      method: 'POST',
      body: JSON.stringify({
        keywords: this.state.keywords,
        uniqueId: Device.getUniqueId(),
      }),
      headers: {
        'Content-Type': 'application/json',
      },
    };
    //store last keyword
    var lastkeywords = this.state.keywords;
    //loading and remove keywords
    await this.setState({
      isLoading: true,
      keywords: null,
    });
    fetch(API.getAPIUrl() + '/assistant/personal', options)
      .then(async response => {
        if (response.status == 400) {
          var json = await response.json();
          throw new Error(json.errorMessage);
        } else {
          return response.json();
        }
      })
      .then(async responseData => {
        //once done appending results
        await this.setState(prevState => ({
          isLoading: false,
          assistants: [...prevState.assistants, responseData],
        }));
        //refresh AdvisorScreen
        await Screen.getAssistantScreen().initialScreen(false);
      })
      .catch(async error => {
        ErrorUtil.handleError('save assistant:', error, this);
        await this.setState(() => ({
          keywords: lastkeywords,
        }));
        //navigate to top up page after 3 seconds
        if (
          error.message != null &&
          error.message.includes('top up your credit')
        ) {
          setTimeout(() => {
            this.props.navigation.navigate('InAppPurchaseScreen');
          }, 3000);
        }
      })
      .finally(async () => {
        await this.setState({isLoading: false});
      });
  }

  createNewThreadForAssitant = assistantId => {
    const {navigation} = this.props;
    navigation.navigate('Advisor', {
      assistantId: assistantId,
    });
  };

  render() {
    const {isLoading} = this.state;
    //using withTranslation the i18n can get from this.props
    const {t} = this.props;
    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <View style={{padding: 5, flexGrow: 1, height: '70%'}}>
            <FlatList
              data={this.state.assistants}
              keyExtractor={(item, index) => index}
              renderItem={({item, index}) => (
                <View
                  style={{
                    margin: 5,
                    flexDirection: 'row',
                    alignItems: 'center',
                  }}>
                  <TouchableOpacity
                    style={{margin: 5, flexGrow: 1, maxWidth: '70%'}}
                    onPress={() => {
                      this.createNewThreadForAssitant(item.id);
                    }}>
                    <Text
                      style={{
                        backgroundColor: '#075E54',
                        borderRadius: 8,
                        padding: 6,
                        borderColor: 'white',
                        borderWidth: 1,
                        fontSize: 20,
                        fontWeight: 'bold',
                        color: 'white',
                      }}>
                      {`${item.name}`}
                    </Text>
                  </TouchableOpacity>

                  <TouchableOpacity
                    style={[
                      styles.button,
                      {
                        height: 42,
                        width: 42,
                        backgroundColor: '#007AFF',
                        alignItems: 'center',
                      },
                    ]}
                    onPress={async () => {
                      this.createNewThreadForAssitant(item.id);
                    }}>
                    <Icon6 name="terminal" size={20} color="black" />
                  </TouchableOpacity>
                  <TouchableOpacity
                    style={[
                      styles.button,
                      {
                        height: 42,
                        width: 42,
                        backgroundColor: '#007AFF',
                        alignItems: 'center',
                      },
                    ]}
                    onPress={async () => {
                      this.deleteAssistant(item);
                    }}>
                    <Icon6 name="trash" size={20} color="black" />
                  </TouchableOpacity>
                </View>
              )}
            />
          </View>

          {ErrorUtil.generateErrorView(this)}
          <View style={styles.inputContainer}>
            <DataBindingTextbox
              ref={input => {
                this.messageInput = input;
              }}
              style={styles.input}
              placeholder={t('assistants.type')}
              placeholderTextColor="gray"
              bindvalue={value => this.dataBind('keywords', value)}
              multiline={true}
              numberOfLines={3}
              maxLength={200}
            />
            <TouchableOpacity
              style={[
                styles.button,
                {backgroundColor: isLoading ? '#454545' : '#499C30'},
              ]}
              onPress={async () => {
                await this.saveAssistant();
              }}
              disabled={isLoading}>
              {isLoading ? (
                <ActivityIndicator size={20} color="#fff" />
              ) : (
                <Icon6 name="floppy-disk" size={20} color="black" />
              )}
            </TouchableOpacity>
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

export default withTranslation()(AdvisorScreen);

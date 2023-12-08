import {React} from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  Dimensions,
  Linking,
  TouchableOpacity,
  SafeAreaView,
  ImageBackground,
} from 'react-native';
import {
  DataBindingComponent,
  DataBindingPicker,
  DataBindingTextbox,
} from 'react-native-twowaybinding';
import API from '../config/Api';
import {ScrollView} from 'react-native-gesture-handler';

import Device from '../utils/Device';
import DeviceInfo from 'react-native-device-info';
import ErrorUtil from '../utils/Error';
import Toast from 'react-native-toast-message';
import Hyperlink from 'react-native-hyperlink';
import FetchUtil from '../utils/Fetch';
import Icon from 'react-native-vector-icons/FontAwesome';
import IconM from 'react-native-vector-icons/MaterialIcons';
import Tts from 'react-native-text-to-speech';
import Screen from '../utils/Screen';
import {withTranslation} from 'react-i18next';
import Clipboard from '@react-native-clipboard/clipboard';
import HistoryScreen from './HistoryScreen';
class AdvisorScreen extends DataBindingComponent {
  constructor(props) {
    super(props);
    Screen.setAssistantScreen(this);
    this.state = {
      messages: [],
      assistantList: [
        {
          id: null,
          name: 'Loading ...',
        },
      ],
      isLoading: true,
      hasScrollbar: false,
      threadId: null,
      showError: false,
      errorMessage: null,
      showRetry: false,
      soundStart: false,
      playingSoundItem: null,
      assistantId: null,
    };
    this.doneCount = 0;
    this.defaultVoiceId = 'en-us-x-sfg-local';
    this.cnVoiceId = 'cmn-cn-x-ccc-local';

    Tts.getInitStatus().then(
      () => {
        /*Tts.voices()
          .then(voices => {
            console.log('found voices:' + voices.length);
          })
          .catch(x => {
            console.error(x);
          });*/
      },
      err => {
        if (err.code === 'no_engine') {
          Tts.requestInstallEngine();
        }
      },
    );
  }

  handleContentSizeChange = (contentWidth, contentHeight) => {
    // Check if the content height exceeds the ScrollView's height
    this.scrollViewcontentHeight = contentHeight;
    this.setHasScrollbar();
  };
  handleScrollViewLayout = event => {
    // Check if the content height exceeds the ScrollView's height
    this.scrollViewHeight = event.nativeEvent.layout.height;
    this.setHasScrollbar();
  };
  async setHasScrollbar() {
    if (this.scrollViewcontentHeight > this.scrollViewHeight) {
      await this.setState({hasScrollbar: true});
      this.scrollView.scrollToEnd(false);
    } else {
      await this.setState({hasScrollbar: false});
    }
  }

  async componentDidMount() {
    Tts.addEventListener(
      'tts-start',
      async event =>
        await this.setState({
          soundStart: true,
        }),
    );

    Tts.addEventListener('tts-finish', async event => {
      if (this.state.playingSoundItem != null) {
        this.state.playingSoundItem.backgroundColor = 'gray';
      }
      await this.setState({
        soundStart: false,
      });
    });

    this.unsubscribeFocus = this.props.navigation.addListener(
      'focus',
      async ev => {
        const {route} = this.props;
        const threadId =
          route.params && route.params.threadId ? route.params.threadId : null;
        const assistantId =
          route.params && route.params.assistantId
            ? route.params.assistantId
            : null;
        if (threadId != null) {
          //clear screen
          await this.setState({messages: []});
          this.readThreadMessage(threadId, true);
          //reset route params
          route.params.threadId = null;
        }
        if (assistantId != null) {
          //set assistant

          await this.setState({
            assistantId: assistantId,
          });
          //create as new
          await this.handleNewMessage();
          //reset route params
          route.params.assistantId = null;
        }
      },
    );
    await this.initialScreen(true);
  }

  async initialScreen(getLastPost) {
    //set device id
    console.log('initialScreen');
    if (Device.getUniqueId() == null) {
      const id = await DeviceInfo.getUniqueId();
      Device.setUniqueId(id);
    }
    this.doneCount = 0;
    //get all assistants
    FetchUtil.fetchWithTimeout(
      API.getAPIUrl() + '/openai/assistants?uniqueId=' + Device.getUniqueId(),
      {},
      5000,
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
        await this.setState({assistantList: responseData});
        //only read last message after the assistants is filling so we will not causing using wrong assistant
        //read last thread by unique Id;
        if (getLastPost && Device.getUniqueId() != null) {
          fetch(
            API.getAPIUrl() +
              `/thread/last/byUniqueId?uniqueId=${Device.getUniqueId()}`,
          )
            .then(async response => {
              if (response.status == 400) {
                var json = await response.json();
                throw new Error(json.errorMessage);
              } else {
                return response.text();
              }
            })
            .then(async responseData => {
              if (responseData != '') {
                responseData = JSON.parse(responseData);
                //assign threadId
                await this.setState({threadId: responseData.threadId});
                this.readThreadMessage(responseData.threadId, false);
              } else {
                this.doneCount = 99;
                this.compeletedLoaded(this.doneCount);
                //todo: show example assistant and questions
                this.showSampleQuestions();
              }
            })
            .catch(error => {
              ErrorUtil.handleError(
                'Error fetching thread/last/byUniqueId:',
                error,
                this,
              );
            })
            .finally(() => {
              this.doneCount += 1;
              this.compeletedLoaded(this.doneCount);
            });
        } else {
          this.doneCount = 99;
          this.compeletedLoaded(this.doneCount);
        }
      })
      .catch(async error => {
        ErrorUtil.handleError(
          'Error fetching /openai/assistants:',
          error,
          this,
        );
        await this.setState({
          showRetry: true,
        });
      })
      .finally(() => {
        this.doneCount += 1;
        this.compeletedLoaded(this.doneCount);
      });
  }

  async showSampleQuestions() {
    const {t} = this.props;
    await this.setState({
      messages: [
        {
          threadId: null,
          messageId: null,
          runId: null,
          replyContext: t('advisor.sampleContext1'),
          assistantId: 'asst_ob8bDUIYy1Rq9NCWP8Qf22UQ',
          uniqueId: null,
          role: 'assistant',
          createTimeStamp: Math.floor(Date.now() / 1000),
          credit: null,
          predictAssistantId: 'asst_ob8bDUIYy1Rq9NCWP8Qf22UQ',
          predictQuestions: [
            t('advisor.sample1Question1'),
            t('advisor.sample1Question2'),
            t('advisor.sample1Question3'),
          ],
        },
        {
          threadId: null,
          messageId: null,
          runId: null,
          replyContext: t('advisor.sampleContext2'),
          assistantId: 'asst_vFcTqm0ScuI1ZkXa3Qb8LEPf',
          uniqueId: null,
          role: 'assistant',
          createTimeStamp: Math.floor(Date.now() / 1000),
          credit: null,
          predictAssistantId: 'asst_vFcTqm0ScuI1ZkXa3Qb8LEPf',
          predictQuestions: [
            t('advisor.sample2Question1'),
            t('advisor.sample2Question2'),
            t('advisor.sample2Question3'),
          ],
        },
        {
          threadId: null,
          messageId: null,
          runId: null,
          replyContext: t('advisor.sampleContext3'),
          assistantId: 'asst_xoCEKl7aCp7imDZP4OxIEjSy',
          uniqueId: null,
          role: 'assistant',
          createTimeStamp: Math.floor(Date.now() / 1000),
          credit: null,
          predictAssistantId: 'asst_xoCEKl7aCp7imDZP4OxIEjSy',
          predictQuestions: [
            t('advisor.sample3Question1'),
            t('advisor.sample3Question2'),
            t('advisor.sample3Question3'),
          ],
        },
      ],
    });
  }

  componentWillUnmount() {
    this.unsubscribeFocus();
  }

  async readThreadMessage(threadId, loading) {
    if (loading) {
      await this.setState({isLoading: true});
    }
    fetch(
      API.getAPIUrl() +
        `/openai/byThreadId?threadId=${threadId}&uniqueId=${Device.getUniqueId()}`,
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
        //set the last thread message into state and show
        //need to set assistantId based on last assistant message
        const lastAssistantMessage = responseData
          .slice() // Create a shallow copy of the array
          .reverse() // Reverse the array to start from the most recent message
          .find(message => message.assistantId !== null);
        var assistantId = this.state.assistantId;
        if (lastAssistantMessage != null) {
          assistantId = lastAssistantMessage.assistantId;
        }
        //if assistantId under assistantList only change
        await this.setState({
          messages: responseData,
          assistantId:
            this.state.assistantList.filter(a => a.id == assistantId).length > 0
              ? assistantId
              : this.state.assistantId,
          threadId: threadId,
        });
      })
      .catch(error => {
        ErrorUtil.handleError('Error fetching openai/byThreadId:', error, this);
      })
      .finally(() => {
        this.doneCount += 1;
        this.compeletedLoaded(this.doneCount);
      });
  }

  async compeletedLoaded(count) {
    if (count >= 3) {
      await this.setState({isLoading: false});
    }
  }

  handleSendMessage = async (message, manual) => {
    const {t} = this.props;
    if (message == null || message == '') {
      await this.setState({
        showError: true,
        errorMessage: t('advisor.messageValidation1'),
      });
      return;
    }
    //hide error message
    await this.setState({
      showError: false,
      errorMessage: null,
    });
    //record last message
    var lastMessage = this.state.message;
    //enable loading
    //appending question

    await this.setState(prevState => ({
      isLoading: true,
      messages: [
        ...prevState.messages,
        {
          threadId: this.state.threadId,
          messageId: null,
          runId: null,
          replyContext: message,
          assistantId: null,
          uniqueId: null,
          role: 'user',
          createTimeStamp: Math.floor(Date.now() / 1000),
          predictQuestions: [],
        },
      ],
    }));
    //record the last max Y
    var y = this.scrollViewcontentHeight;
    //clear message if user send manually
    if (manual) {
      await this.setState({message: null});
    }
    //call API
    const options = {
      method: 'POST',
      body: JSON.stringify({
        threadId: this.state.threadId,
        assistantId: this.state.assistantId,
        messageContext: message,
        uniqueId: Device.getUniqueId(),
      }),
      headers: {
        'Content-Type': 'application/json',
      },
    };
    fetch(API.getAPIUrl() + '/openai/message', options)
      .then(async response => {
        if (response.status == 400) {
          var json = await response.json();
          throw new Error(json.errorMessage);
        } else {
          return response.json();
        }
      })
      .then(async responseData => {
        //history need refresh in next if new thread
        if (this.state.threadId == null) {
          HistoryScreen.needRefresh();
        }
        //display only when the responseData threadId = current threadId or new thread
        if (
          this.state.threadId == null ||
          this.state.threadId == responseData.threadId
        ) {
          //once done appending answer and disable loading
          //also set the threadId in case it is new thread
          await this.setState(prevState => ({
            isLoading: false,
            threadId: responseData.threadId,
            messages: [...prevState.messages, responseData],
            errorMessage: `Remaining credit: ${responseData.credit}`,
            showError: true,
          }));
          //scroll to the answer
          if (this.scrollView != null) {
            this.scrollView.scrollTo({x: 0, y: y, animated: true});
          }
        }
      })
      .catch(async error => {
        ErrorUtil.handleError('Send message:', error, this);
        //reverse last message
        this.state.messages.pop();
        if (this.state.message == null || this.state.message == '') {
          //assign back the last message and do messages refresh
          await this.setState(() => ({
            message: lastMessage,
          }));
        } else {
          //do messages refresh
          await this.setState(() => ({}));
        }
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
  };

  handleNewMessage = async () => {
    await this.setState({threadId: null, messages: []});
    //focus message input
    if (this.messageInput.control != null) {
      this.messageInput.control.focus();
    }
  };
  timestampToDateTime = timestamp => {
    const date = new Date(timestamp * 1000); // Convert seconds to milliseconds

    // Get the date components
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    const formattedDateTime = `${day}/${month} ${hours}:${minutes}:${seconds}`;

    return formattedDateTime;
  };

  containsChinese = text => {
    // Use a regular expression to check for Chinese characters
    const chineseRegex = /[\u4e00-\u9fa5]/; // This regex matches Chinese characters

    return chineseRegex.test(text);
  };

  containsArabic = text => {
    const arabicRegex = /[\u0600-\u06FF]/;
    return arabicRegex.test(text);
  };

  speakText = async (text, item, allItem) => {
    for (let i = 0; i < allItem.length; i++) {
      allItem[i].backgroundColor = 'gray';
    }

    if (this.state.soundStart) {
      Tts.stop();
      item.backgroundColor = 'gray';
    }
    if (item != this.state.playingSoundItem) {
      item.backgroundColor = 'green';
      //change default voice based on language
      var isArabic = this.containsArabic(text);

      if (!isArabic) {
        var isChinese = this.containsChinese(text);
        if (!isChinese) {
          Tts.setDefaultVoice(this.defaultVoiceId);
        } else {
          Tts.setDefaultVoice(this.cnVoiceId);
        }
        Tts.speak(text, {
          androidParams: {
            KEY_PARAM_PAN: 0,
            KEY_PARAM_VOLUME: 1.0,
            KEY_PARAM_STREAM: 'STREAM_ACCESSIBILITY',
          },
        });
        await this.setState({
          playingSoundItem: item,
        });
      }
    } else {
      await this.setState({
        playingSoundItem: null,
      });
    }
  };

  render() {
    const {isLoading} = this.state;
    //using withTranslation the i18n can get from this.props
    const {t} = this.props;
    return (
      <SafeAreaView style={{flex: 1}}>
        <View style={styles.container}>
          <ImageBackground
            source={require('./../assets/chat.jpg')}
            style={styles.image}>
            <View style={styles.pickerContainer}>
              <DataBindingPicker
                style={styles.picker}
                items={this.state.assistantList}
                itemStyles={styles.pickerItem}
                itemLabel="name"
                itemValue="id"
                bindvalue={value => this.dataBind('assistantId', value)}
              />
              <TouchableOpacity
                style={[
                  styles.newMessageButton,
                  {backgroundColor: isLoading ? '#454545' : 'lightgreen'},
                ]}
                onPress={this.handleNewMessage}
                disabled={isLoading}>
                <Text
                  style={{
                    color: isLoading ? 'white' : 'black',
                    fontSize: 18,
                    height: 40,
                    textAlignVertical: 'center',
                  }}>
                  {t('advisor.newMessage')}
                </Text>
              </TouchableOpacity>
            </View>

            <ScrollView
              showsVerticalScrollIndicator={true}
              style={{
                width: '100%',
                flexDirection: this.state.hasScrollbar
                  ? 'column'
                  : 'column-reverse',
              }}
              ref={scrollView => {
                this.scrollView = scrollView;
              }}
              onContentSizeChange={(contentWidth, contentHeight) => {
                this.handleContentSizeChange(contentWidth, contentHeight);
              }}
              onLayout={event => this.handleScrollViewLayout(event)}>
              <View style={{padding: 5}}>
                <FlatList
                  scrollEnabled={false}
                  data={this.state.messages}
                  keyExtractor={(item, index) => index}
                  renderItem={({item, index}) => (
                    <View>
                      <View
                        style={{
                          flexDirection:
                            item.assistantId != null ? 'row' : 'row-reverse',
                          alignItems: 'center',
                        }}>
                        <TouchableOpacity
                          onLongPress={() => {
                            const {t} = this.props;
                            Clipboard.setString(item.replyContext);
                            Toast.show({
                              text1: t('advisor.clipboard'),
                              text2: t('advisor.copy'),
                            });
                          }}
                          style={{width: '80%'}}>
                          <Hyperlink
                            onPress={(url, text) => Linking.openURL(url)}
                            linkStyle={{
                              fontSize: 18,
                              color: '#61A2FF',
                              textDecorationLine: 'underline',
                            }}>
                            <Text
                              style={{
                                backgroundColor:
                                  item.assistantId == null
                                    ? '#075E54'
                                    : '#DBD7CC',
                                width: '100%',
                                borderWidth: 1,
                                borderColor:
                                  item.assistantId == null ? 'white' : 'blue',
                                borderRadius: 8,
                                padding: 6,
                                fontSize: 16,
                                fontWeight: 'bold',
                                color:
                                  item.assistantId == null ? 'white' : 'black',
                              }}>{`${item.replyContext}`}</Text>
                          </Hyperlink>
                        </TouchableOpacity>
                        <TouchableOpacity
                          style={[
                            styles.sendButton,
                            {
                              backgroundColor:
                                item.backgroundColor == null
                                  ? 'gray'
                                  : item.backgroundColor,
                              height: 48,
                              width: 48,
                              alignItems: 'center',
                              marginRight: item.assistantId == null ? 10 : 0,
                              display: this.containsArabic(item.replyContext)
                                ? 'none'
                                : 'flex',
                            },
                          ]}
                          onPress={async () => {
                            item.backgroundColor = 'green';
                            await this.setState({});
                            await this.speakText(
                              item.replyContext,
                              item,
                              this.state.messages,
                            );
                          }}>
                          <Icon
                            name={
                              item.backgroundColor == null ||
                              item.backgroundColor == 'gray'
                                ? 'volume-up'
                                : 'stop'
                            }
                            size={28}
                            color="black"
                          />
                        </TouchableOpacity>
                      </View>
                      <Text
                        style={{
                          alignSelf:
                            item.assistantId != null
                              ? 'flex-start'
                              : 'flex-end',
                          color: 'white',
                        }}>
                        {`${this.timestampToDateTime(item.createTimeStamp)}`}
                      </Text>
                      {item.predictQuestions != null &&
                        item.predictQuestions.length > 0 && (
                          <FlatList
                            scrollEnabled={false}
                            data={item.predictQuestions}
                            keyExtractor={(item, index) => index}
                            renderItem={childItem => (
                              <View
                                style={{
                                  marginBottom: 3,
                                  marginTop: 3,
                                  flexDirection: 'row',
                                  alignItems: 'center',
                                }}>
                                <TouchableOpacity
                                  onPress={async () => {
                                    //set respective assistant id if it is not null
                                    if (item.predictAssistantId != null) {
                                      await this.setState({
                                        assistantId: item.predictAssistantId,
                                      });
                                      //this.state.assistantId = item.predictAssistantId
                                    }
                                    this.handleSendMessage(
                                      childItem.item,
                                      false,
                                    );
                                  }}
                                  onLongPress={() => {
                                    const {t} = this.props;

                                    Clipboard.setString(childItem.item);
                                    Toast.show({
                                      text1: t('advisor.clipboard'),
                                      text2: t('advisor.copy'),
                                    });
                                  }}
                                  disabled={isLoading}
                                  style={{width: '80%'}}>
                                  <Hyperlink
                                    onPress={(url, text) =>
                                      Linking.openURL(url)
                                    }
                                    linkStyle={{
                                      fontSize: 18,
                                      color: '#61A2FF',
                                    }}>
                                    <Text
                                      style={{
                                        alignSelf: 'flex-start',
                                        backgroundColor: isLoading
                                          ? '#454545'
                                          : '#0F0E38',
                                        width: '100%',
                                        borderWidth: 1,
                                        borderColor: 'white',
                                        borderRadius: 8,
                                        padding: 6,
                                        fontSize: 16,
                                        fontWeight: 'bold',
                                        color: 'white',
                                      }}>{`${childItem.item}`}</Text>
                                  </Hyperlink>
                                </TouchableOpacity>
                                <TouchableOpacity
                                  style={[
                                    styles.sendButton,
                                    {
                                      backgroundColor: 'gray',
                                      height: 48,
                                      width: 48,
                                      alignItems: 'center',
                                      display: this.containsArabic(
                                        childItem.item,
                                      )
                                        ? 'none'
                                        : 'flex',
                                    },
                                  ]}
                                  onPress={async () => {
                                    await this.setState({});
                                    this.speakText(
                                      childItem.item,
                                      childItem,
                                      this.state.messages,
                                    );
                                  }}>
                                  <Icon
                                    name="volume-up"
                                    size={28}
                                    color="black"
                                  />
                                </TouchableOpacity>
                              </View>
                            )}
                          />
                        )}
                    </View>
                  )}
                />
              </View>
            </ScrollView>

            {this.state.showRetry && (
              <View style={[styles.container, {justifyContent: 'flex-start'}]}>
                <TouchableOpacity
                  style={[styles.sendButton, {backgroundColor: '#B88611'}]}
                  onPress={async () => {
                    await this.setState({isLoading: true});
                    await this.initialScreen(true);
                    await this.setState({
                      showRetry: false,
                      showError: true,
                    });
                  }}>
                  <IconM name="refresh" size={40} color="black" />
                </TouchableOpacity>
              </View>
            )}

            {ErrorUtil.generateErrorView(this)}
            <View style={styles.inputContainer}>
              <DataBindingTextbox
                ref={input => {
                  this.messageInput = input;
                }}
                style={styles.input}
                placeholder={t('advisor.type')}
                placeholderTextColor="white"
                bindvalue={value => this.dataBind('message', value)}
                multiline={true}
              />
              <TouchableOpacity
                style={[
                  styles.sendButton,
                  {backgroundColor: isLoading ? '#454545' : '#007AFF'},
                ]}
                onPress={() => {
                  this.handleSendMessage(this.state.message, true);
                }}
                disabled={isLoading}>
                {isLoading ? (
                  <ActivityIndicator size={20} color="#fff" />
                ) : (
                  <Icon name="send" size={20} color="black" />
                )}
              </TouchableOpacity>
            </View>
          </ImageBackground>
        </View>
        <Toast
          type="success"
          position="top"
          visibilityTime={2000} // Adjust as needed
          autoHide={true}
        />
      </SafeAreaView>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'black',
  },
  header: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 10,
  },
  picker: {
    flex: 1,
    alignItems: 'center', // Center horizontally
    justifyContent: 'center', // Center vertically
    width: Dimensions.get('window').width * 0.5,
    color: '#3EA621',
  },
  pickerItem: {
    fontSize: 18,
  },
  pickerContainer: {
    flexDirection: 'row',
    margin: 5,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 1, // Add a border
    borderColor: 'black', // Border color
    backgroundColor: 'white',
  },
  inputContainer: {
    flexDirection: 'row',
    padding: 4,
    height: '100',
    width: '100%',
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
  sendButton: {
    backgroundColor: '#007AFF',
    borderRadius: 8,
    marginLeft: 10,
    padding: 10,
  },
  newMessageButton: {
    backgroundColor: 'lightgreen',
    borderRadius: 2,
    margin: 0,
    padding: 2,
  },
  image: {
    width: '100%',
    height: '100%',
    resizeMode: 'cover',
    borderRadius: 10,
    //justifyContent: 'center',
    //alignItems: 'center',
  },
});

export default withTranslation()(AdvisorScreen);

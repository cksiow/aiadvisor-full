export default class Screen {
    static assistantScreen = null;
    constructor() {
  
     }
  
    static setAssistantScreen(assistantScreen){
        Screen.assistantScreen = assistantScreen
    }
  
    static getAssistantScreen(){
      return Screen.assistantScreen
    }
  
  }
  
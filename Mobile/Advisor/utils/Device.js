export default class Device {
  static uniqueId = null;
  constructor() {

   }

  static setUniqueId(uniqueId){
    Device.uniqueId = uniqueId
  }

  static getUniqueId(){
    return Device.uniqueId
  }

}

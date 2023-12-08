export default class Fetch {


  
   static async fetchWithTimeout(url, options, timeout = 180000) {
    return Promise.race([
        fetch(url, options),
        new Promise((_, reject) => setTimeout(() => reject(new Error('timeout')), timeout))
    ]);
  }
}
  
# WeatherApp
Weather app

- Known Issues:
  The city list JSON file is too big so on my emulator it takes around 7 seconds to load. The bottleneck is json deserialisation.
- Possible solution:
  1. Convert json to csv and only keep the id, name, country field. The file size will be much smaller and csv format is easier to parse than JSON format.
  2. Create a big java list with all the data already initialized. No need to read the json file and parse it.

- code design:
  A lot of code comes from https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample/ 
  The csv will be loaded in a big list and then get sorted, then we can use binarysearch to find the matched city name by the first several characters. Such match operation will take much longer(around 40ms on my emulator) without using binarySearch and compare one by one in the list of more than 20,000 elements. It takes less than 1ms with binarySearch.
  
- Choices of app libraries:
  * Android+kotlin library
  * Retrofit2+OKHTTP+Moshi
  * Koin for dependency injection
  * gps location permission: easypermissions and play-services-location
  * preference library 'com.scottyab:secure-preferences-lib:0.1.7' and implementation "com.f2prateek.rx.preferences2:rx-preferences:2.0.0" with the ability to save object in preferences. 
  * glide: to load weather icon file
 - Test libraries
  * mockk: simple mock library
  * mockwebserver: to test if the response can be parsed
- use of UI paradigms: MVVM + liveData

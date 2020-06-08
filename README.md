# WeatherApp
Weather app

Known Issues:
  The city list JSON file is too big so on my emulator it takes around 7 seconds to load. The bottleneck is json deserialisation.
Possible solution:
  A: Convert json to csv and only keep the id, name, country field. The file size will be much smaller and csv format is easier to parse than JSON format.
  B: Create a big java list with all the data already initialized. No need to read the json file and parse it.
 
code design:
  A lot of code comes from https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample/ 
  The csv will be loaded in a big list and then get sorted, then we can use binarysearch to find the matched city name by the first several characters. Such match operation will take much longer(around 40ms on my emulator) without using binarySearch and compare one by one in the list of more than 20,000 elements. It takes less than 1ms with binarySearch.
  
Choices of libraries:
  Android+kotlin library
  Retrofit2+OKHTTP+Moshi
  Koin
test coverage

use of UI paradigms: MVVM

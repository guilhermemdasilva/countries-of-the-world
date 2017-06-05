# Countries of the World for Android

This is a simple app to get basic information about several countries of the world

## About the App

Countries of the World is an app made as a task which is part of the application process at Rebtel

## Requirements for the assignment

1. Select Country
2. Display Country's Information
3. Show Country's Flag
4. Use REST Countries API

## Development process

Based on the given requirements, I made a list of thing that I think this app should have. I will list them below.

* RecyclerView: My first idea was to make a list of flags. For that, I used the RecyclerView and the CardView since they work very well together, besides they are very flexible, making the layout task easier.

* Volley: Since the REST API needed to be accessed, I chose Volley as a library to perform the network operations. I chose this mainly because I had worked with it before.

* Butter Knife: to make view binding simpler and the code a little bit cleaner.

* Unit testing: for better quality of code, allowing less bugs and side effects.

* Fragments: to show the country's information, a fragment is created containing a few informations from the API.

* Check and Request permissions at runtime for API 23+

* Present the user with a few error messages as a feedback in case something go wrong.

* Search View: as the user iteracts with the list, since it is a big one, I thought it would be better to have a Search View for better UX.

* Separete classes in different packages: this help any developer to understand better the code. The organization of a MVC-like structure is vry familiar and easy to understand.

The whole process was thought to be like a Sprint in SCRUM, since I wanted to finish in a few days.

In the last day, I decided to add a MapBox map to the app to make it more interesting to use. For that, I got a limited access token, which I believe anyone who can run the code will be able to use it.

For the environment, I used Ubuntu with Android Studio as IDE. For testing, I used a Samsung Galaxy Note 5.

## Conclusion

Despite being a simple app, the openness of the requirements made me think about what is really important to showcase while developing an Android app. For the most part, I believe all the requirements were fulfilled and the quality of the development is good enough to suit the team needs.

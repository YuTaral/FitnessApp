FitnessApp

FitnessApp is a mobile application developed in Kotlin that allows users to save and manage their workouts. The application utilizes a .NET Web API as its backend service.
Features

    Log and track workouts
    View workout history
    Create different exercises for muscle group and add them to your workout
    Create workout templates, which can be easily reused
    User profile and exercise default values (sets, reps, weight and rest) global for the user or specific for exercise
    Team management - designed for coaches to create teams, invite members and assign workouts to them, so they can easily track the client's progress
   
Prerequisites

Before running the application, ensure you have the following installed: Android Studio

Getting Started

To set up and run the application locally, follow these steps:
Set Up the Backend API:
    Clone the backend API repository from FitnessAppAPI.
    Follow the instructions in the backend repository to set up and run the API.

Configure the Frontend Application:
    Clone this repository: git clone https://github.com/YuTaral/FitnessApp.git

Open the project in Android Studio.
Ensure a local.properties file exists in the root folder. If it's missing, create one.
Add the following line to local.properties, replacing 192.168.0.0 and 1111 with the appropriate values from your backend API's launchSettings.json:

DEV_BASE_URL=http://192.168.0.0:1111/api/

Run the Application:
Start the backend API service.
In Android Studio, run the application on an emulator or connected device.

License
This project is licensed under the MIT License.

Contact
For any inquiries or feedback, please contact me via LinkedId - https://www.linkedin.com/in/yusuf-taral-1a0922229/. Both the frontend and backend applications are currently under development. Contributions are welcome!

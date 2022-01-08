# Smart Reminders Application
This project was my final project, and it was about android application development, and we were 2 partners.
We took care to write flexible and documented code, and to build application that it's very easy to use and understand.

This is a video that explains and introduces the application:
https://drive.google.com/file/d/1Lbzkf1gmXA3kXOeWJaCGrDFxrPKmqhpA/view?usp=sharing


# Main idea:
The application contains two main features:
1) Alarm clock.
2) Rminders:
      - Regular reminders (Todo list)
      - Reminders based time
      - Reminders based location

# Tools:
Programming Language: Java (IDE: Android Studio)
Emulator: Nexus 5X API 28.
Database: Firebase real-time database.
We use Cloud Storage for recording audio.
We use google Places API to get a place by category.
We use google autocomplete feature to get a specific place.


# Application Description:
  1) Login /Signup/forget password page
  2) Home page
  3) Alarm clock
  4) Regular reminders
  5) Reminders based location/time.

# Login:
This is the first page of the application, you can login if you already have an account.
(when you open the application and you don't logout ever then it will directly open the Home page even though you do kill to the process)

![image](https://user-images.githubusercontent.com/97045152/148053046-0fcbff18-28e2-419f-89e7-cb59988aabd2.png)

# Sign up:
If you don't have an account, you can do this by clicking on NOT A MEMBER YET? and it will open the below page:

![image](https://user-images.githubusercontent.com/97045152/148053340-dd43251b-fdae-40f3-bf99-9b27ac854493.png)

# Retrieve password:
If you forget your password, then you can retrieve it by clicking on FORGET PASSWORD?, and it will open the below page:

![image](https://user-images.githubusercontent.com/97045152/148053499-258eda6e-82cb-4cda-b996-b357e31f94d0.png)


# Home page:
This the homepage of the application, and it contains:
  - A greeting to the user
  - The button + to add a new reminder
  - All the reminders list based on the choosed category by the filter (all the reminders, reminders based location, reminders based time and todo list)
  and by pressing on any reminder, and according to the type of the reminder, it will open the appropriate page that initialized with the old data.
  - The navigation drawer in the upper left corner.


![image](https://user-images.githubusercontent.com/97045152/148054038-3a2ef196-f972-491a-bce2-61338125703b.png)
![image](https://user-images.githubusercontent.com/97045152/148054943-309d3c2e-148d-4a3f-a81b-9e4d6cb0a09f.png)


# Alarm clock:
By clicking on the Alarm clock on the navigation drawer it will open this page:

![image](https://user-images.githubusercontent.com/97045152/148055491-ea0c322e-8b69-43bc-ab6a-0d41065ea836.png)

  - We can add a title to the alarm.
  - To set the time and it automatically set it to today or tomorrow according to the current time and the selected time.
  - Set the alarm to specific date using the calendar (the button on the tight side)
  - Set repeated alarm by selecting the desired days
  - When the alarm rings we get a notification with two options: snooze and delete.


# regular alarms:
When we press on the + button in the main page we get the below buttons:

![image](https://user-images.githubusercontent.com/97045152/148056675-b8c21598-5e13-4a0d-85d5-d073e4e23462.png)

If we press on the pencil we open the regular reminder: 

![image](https://user-images.githubusercontent.com/97045152/148056862-92daa7cf-8ecf-4c83-81ed-e6fc0a5de8fe.png)

You can write the title or description or recording audio.


# Reminders based location/time:
By pressing on the location button, it will open the below page:

![image](https://user-images.githubusercontent.com/97045152/148057106-3da6a343-da39-4a95-8338-6adac5a05243.png)

* The left down button is to add description with option to record audio.
  In order to show or to edit the description you can click back on it, and it will display the last version that you saved.
  
* The middle button is to set time.

* The right button open the below page:
  
  ![image](https://user-images.githubusercontent.com/97045152/148059786-27258900-c9ef-41fd-bb57-d45e49cbf230.png)

  We can choose one from  three options:
    - Category: when we get the notification we can choose to stop and delete it or to suggests the second closest place.
    - Specific location: we must choose the place using the auto complete, and when get the notification we can stop it or to snooze.
      
      ![image](https://user-images.githubusercontent.com/97045152/148060001-e658ca1b-b108-44b8-b9a4-81b2d501a59e.png)
      
    - User in the application: we must write his email, also we can choose to stop the notification or to snooze.
      
      ![image](https://user-images.githubusercontent.com/97045152/148059912-6d962017-dd85-4210-9561-a11de3084528.png)
      
    On all those three options the reminder will rings when you are X kilometer from the target. (X was declared in the code).

  
  Exampel for reminder:
  
  ![image](https://user-images.githubusercontent.com/97045152/148062026-7df26010-2351-4c99-beb3-16946674e531.png)











 

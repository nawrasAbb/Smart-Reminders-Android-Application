# Smart Reminders Application
This project was my final project, and it was about android application development, and we were 2 partners.
We took care to write flexible and documented code, and to build application that it's very easy to use and understand.

This is a video that explains and introduces the application:
https://drive.google.com/file/d/1Lbzkf1gmXA3kXOeWJaCGrDFxrPKmqhpA/view?usp=sharing

# Tools:
- Programming Language: Java (IDE: Android Studio).
- Database: Firebase real-time database.
- We use Cloud Storage to store recordings.
- We use Google Places API to find a place by category.
- We use Google Autocomplete feature to find a specific place.





# Main idea:
The application contains two main features:
1) Alarm clock.
2) Reminders:
      - Regular reminders (Todo list)
      - Reminders based time
      - Reminders based location




# Application Description:
  1) Login /Signup/forget password pages
  2) Home page
  3) Alarm clock
  4) Regular reminders
  5) Reminders based location/time.

# Login:
This is the first page of the application, you can login if you already have an account.
(If you already have an account and you don't logout ever, then it will directly open the Home page even though you killed the process)

![image](https://user-images.githubusercontent.com/97045152/148053046-0fcbff18-28e2-419f-89e7-cb59988aabd2.png)

# Sign up:
If you don't have an account, you can do this by clicking on NOT A MEMBER YET? and it will open the below page:

![image](https://user-images.githubusercontent.com/97045152/148053340-dd43251b-fdae-40f3-bf99-9b27ac854493.png)

# Retrieve password:
If you forget your password, then you can retrieve it by clicking on FORGET PASSWORD?, and it will open the below page:

![image](https://user-images.githubusercontent.com/97045152/148053499-258eda6e-82cb-4cda-b996-b357e31f94d0.png)


# Home page:
This the home page of the application, and it contains:
  - A greeting to the user
  - The button + to add a new reminder
  - All the reminders list based on the choosed category by the filter (all the reminders, reminders based location, reminders based time and todo list). 
  - By pressing on any saved reminder, and according to its type, it will open the appropriate page that initialized with the old data inorder to edit it.
  - The navigation drawer in the upper left corner.


![image](https://user-images.githubusercontent.com/97045152/148054038-3a2ef196-f972-491a-bce2-61338125703b.png)
![image](https://user-images.githubusercontent.com/97045152/148054943-309d3c2e-148d-4a3f-a81b-9e4d6cb0a09f.png)


# Alarm clock:
By clicking on the Alarm clock on the navigation drawer it will open this page:

![image](https://user-images.githubusercontent.com/97045152/148055491-ea0c322e-8b69-43bc-ab6a-0d41065ea836.png)

  - We can add a title to the alarm.
  - We can set the alarm to specific date using the calendar (the button on the right side).
  - We can set repeated alarm by selecting the desired days.
  - If we don't choose any day or specific date, then it automatically set the alarm to today or tomorrow according to the current time and the selected time.
  - When the alarm rings we get a notification with two options: snooze and delete.


# regular alarms:
When we press on the + button in the main page we get the below buttons:

![image](https://user-images.githubusercontent.com/97045152/148056675-b8c21598-5e13-4a0d-85d5-d073e4e23462.png)

If we press on the pencil we open the regular reminder: 

![image](https://user-images.githubusercontent.com/97045152/148056862-92daa7cf-8ecf-4c83-81ed-e6fc0a5de8fe.png)

You can write the title or description or recording audio.


# Reminders based location/time:
By pressing on the + button and then on the location button, it will open the below page:

![image](https://user-images.githubusercontent.com/97045152/148057106-3da6a343-da39-4a95-8338-6adac5a05243.png)

* The left down button is to add description with option to record audio.
  In order to show or to edit the description you can click back on it, and it will display the lastest version you saved.
  
* The middle button is to choose time.

* The right button open the below page:
  
  ![image](https://user-images.githubusercontent.com/97045152/148059786-27258900-c9ef-41fd-bb57-d45e49cbf230.png)


We can choose one of three options:
1) Category: 
- The user chooses category from a given category set.
- When the reminder starts to run (at the selected time) the application starts to suggest the most closest place that belonging to the selected category, which is at least x kilometer away(x was declared in the code).
- When the user get the notification, he can choose to stop and delete it, or to give him the next suggestion (the next closest place).

2) Specific location:
- By clicking on the + button at the categories page, it displays to him the autocomplete inorder to choose a specific place.
- The user get the notification when he is x kilometer away (x defined in the code).
- When the user get the notification, he can choose to stop and delete it, or to snooze. 


![image](https://user-images.githubusercontent.com/97045152/148060001-e658ca1b-b108-44b8-b9a4-81b2d501a59e.png)




3) User in the application:
- By clicking on the user emoji at the categories page, it desplays to the user a text file inorder to write the email of another user in the application that he wants to remember something when he is near him. 
- When the user get the notification, he can choose to stop and delete it, or to snooze. 

![image](https://user-images.githubusercontent.com/97045152/148059912-6d962017-dd85-4210-9561-a11de3084528.png)


Exampel for reminder:
  
![image](https://user-images.githubusercontent.com/97045152/148062026-7df26010-2351-4c99-beb3-16946674e531.png)

Also we can delete or edit any option.










 

**App Summary & Purpose**

The Weight Tracking App was designed to help users track their weight progress towards a set goal.
The app allows users to input their weight, store data over time, set goals, and receive feedback on their progress
thorugh in app notifications. Additionally, users can enable SMS notifications to receive updates when they move 
closer to or further away from their goal. The primary goal of this app was to provide real-time motivation and
progress tracking to encourage users to stay on track with their fitness or weight-related goals.

**User Needs/ UI design**

To support the users needs the app includes the following key screens and features:

**Dashboard Screen:** This displays the current goal weight, recent weight entries, progress notifications,
and also a chart that will be completed in a later update.  This will show the trending progress over time.

**Notification Screen:** Allows users to turn on SMS notifications and input their phone number. 

**Data Management Features:** Users can create, read, update, and delete weight entries, and goals to track changes over time.
This CRUD functionality is apart of the key functionality of the app. 

**Development Approach**

This was done through a more iterative approach where I added features incementally.    Those features would be tested
and then edited until they worked appropriately.  I created code more modularly, and kept the app broken into separate
components.  This was for the business logic, to the view, and the different classes used. For example data managment, UI,
and notifications are all separate.  

Implemented a SQLite database to store entries efficiently.

Permission handling was added to ensure that the app requests the nessesary permissions. 

Also utilized user centered feedback with in app notifications and SMS messaging to provide real time feedback for all weight entries. 

I think these strategies and techniques will be valuble in future projects by making my development more organized, scalable, and maintainable. 

**Testing:**

For testing, I would run the application in the emulator.  If I recieved errors I would look up the errors, and debug one at a time.
If the app ran, I would check the logic of what I was trying to accomplish, and then tried edge testing the logic to see if it
worked in unique situations. I think in the future, I could actually write JUnit tests to spacifically test each bit of the code.

**Challenges**
One major challenge was implementing SMS notifications. I ran into android sms permission restrictions.  This lead to the SMS permission in 
the AndroidManifes.xml file not being enough.  The app needed to spacifically request permission at runtime before sending SMS notifications.
Some other challenges with SMS was handling missing or incorrect phone numbers, and ensuring the SMS was sent at the right time. 

To fix these problems I overcame the restrictions by implemting runtime permission handling.  I also implemented a method called sendSMSNotificationFeedback that
checks if the SMS notifications are enabled, determines whether to send positive or negative feedback, and sends and SMS immediately after displaying the in app notification.


Another challenge was with how the notifications would display based on the weight entries that were entered.  This was a logic challenge.
This took a little time, but was able to find a sweet spot for adding.  Before I mistakenly had it to where the app 
only compared the most recent entry against the goal weight.  If the user had an initial weight of 200lbs and the goal was 180 and then
recorded 190 the app would correctly show positive feedback initially.  But when the user entered another weight, lets say 195 lbs, the 
app would display an incorrect positive feedback message, and did not recognize that they were moving away from their goal of 180 based off of the last entry.
To fix this I implemented logic that stores the previous weight entry before inserting a new weight.  It then compares the latest entry to both the goal and the previous weight.
Lastly it determines if the user is improving or getting closer to the goal, or moving further away from the goal.  Then based off of that it sends eaither positive
or negative feedback. 

**Demonstrating Knowledge**
Developing the Weight Tracking App required applying a variety of skills, from initial planning and user requirement 
analysis to implementing full CRUD (Create, Read, Update, Delete) functionality and ensuring a seamless user experience 
with notifications and data management. Two areas where I demonstrated strong technical competency were (1) designing and
implementing a functional dashboard with complete CRUD capabilities and (2) analyzing user needs to plan the app structure
before development.

One of my biggest achievements in this project was implementing a fully interactive dashboard that provided users
with an intuitive way to track their weight progress over time. The dashboard was designed to display relevant weight data
while allowing users to add, edit, and delete weight entries seamlessly.

A key aspect of the dashboard was integrating full CRUD functionality, which allowed users to:

Create a weight entry (add a new weight and associate it with a date).
Read weight history (display all weight entries in a structured list).
Update an existing weight entry (modify the recorded weight or date).
Delete an entry if needed (remove an incorrect or outdated weight record).
Each CRUD operation was tied to the database layer using an SQLite-based DatabaseHelper class, ensuring data persistence. 
The challenge was ensuring that all operations were intuitive and worked efficiently, even when users modified their entries multiple times.







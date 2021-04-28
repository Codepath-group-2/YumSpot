# YumSpot

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview
### Description
YumSpot is an app that allows users can search good places to eat, upload and share photos of food, and make appointments to eat out.

### App Evaluation
- **Category:** Social Networking / Food
- **Mobile:** This app would be developed for mobile. Functionality would be limited to mobile devices. This app uses map and camera.
- **Story:** users can search good places to eat, upload and share photos of food, and make appointments to eat out.
- **Market:** Any individual could choose to use this app whoever enjoys food and likes to eat out. Sharing photos and talking with friends about food will allow users to have more exciting experience.
- **Habit:** This app could be used as often or unoften as the user wanted depending on how often they are talking about food and eating out.
- **Scope:** First we would start with sharing food interests. We would like to expand to delivery and advertisement features.


## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**
- [x] Users can log in and sign up for the app.
- [x] Users can access previous chats.
- [x] Users can search restaurants by location and name.
- [x] Users can select food categories to see the list of restaurants.
- [x] Users can see details of restaurants.
- [x] Users can post photos of food. 
- [x] Users can talk with their friend.
- [x] Settings (profile, logout)

**Optional Nice-to-have Stories**

- [ ] Include a map on detailed page of restaurant. 
- [ ] Share map in chat.
- [ ] Users can see their preferred restaurants.
- [x] Show posted photos by a user on profile page.
- [ ] Include a like button to save user's favorite places.
- [x] Include a phone call feature

### 2. Screen Archetypes

* Login Screen
   * Users can log in
* Register Screen
   * Users can register a new account
* Category Screen
   * Food category buttons 
* Search Screen
   * Users can search restaurants by name or location.
   * Users can see the list of restaurants.
* Restaurant Info Screen
   * Users can see information of a restaurant such as phone number, location, opening hours, etc.
* Feed Screen
   * Users can view photos from other users.
* Post Screen
   * Users can post photos and comment on feed.
* Chat Screen
   * Allows user to chat with their friends
* Settings Screen
   * Lets users can go to their profile page and log out.
* Profile Screen
   * Users can view their own posts.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Restaurant
* Feed
* Chat
* Settings

**Flow Navigation** (Screen to Screen)
* Login Screen (Forced Log-in) -> Register Screen (Account creation if no log in is available)
* Category Screen (selection) -> Search Screen -> Restaurant Info Screen
* Feed Screen -> Post Screen (if post button is clicked)
* Chat Screen
* Settings Screen -> Profile Screen (if profile button is clicked)
                  -> Login Screen (if logout button is clicked)

### [BONUS] Digital Wireframes & Mockups
<img src="https://github.com/Codepath-group-2/codepath-group2/blob/master/YumSpot.JPG" height=600>

### [BONUS] Interactive Prototype
<img src="https://github.com/Codepath-group-2/codepath-group2/blob/master/progress.gif" width=300>


## Schema 
### Models
#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | image         | File     | image that user posts |
   | userId        | String   | unique id for the user post (default field) |
   | user          | Pointer to User | image user |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   | userEmail     | String   | user's email |
   | userPassword  | String   | user's password |
   
### Networking
#### List of network requests by screen
   - Register Screen
      - (Create/POST) Create a new account
         ```
         // Create the ParseUser
         ParseUser user = new ParseUser();
         // Set core properties
         user.setUserId("joestevens");
         user.setUserPassword("secret123");
         user.setUserEmail("email@example.com");
         // Invoke signUpInBackground
         user.signUpInBackground(new SignUpCallback() {
           public void done(ParseException e) {
             if (e == null) {
               // Hooray! Let them use the app now.
             } else {
               // Sign up didn't succeed. Look at the ParseException
               // to figure out what went wrong
             }
           }
         });
         ```
   - Feed Screen
      - (Create/POST) Create a new comment on a post
         ```
         @ParseClassName("Post")
         public class Post extends ParseObject {
            // ...
         }
 
         @ParseClassName("Comment")
         public class Comment extends ParseObject {
                 // ...
 
           // Associate each comment with a user
           public void setOwner(ParseUser user) {
             put("owner", user);
           }
 
           // Get the user for this comment
           public ParseUser getOwner()  {
                    return getParseUser("owner");
           }
 
           // Associate each comment with a post
           public void setPost(Post post) {
             put("post", post);
           }
 
           // Get the post for this item
           public Post getPost()  {
                    return (Post) getParseObject("post");
           }
         }
 
         // Create the post
         Post post = new Post("Welcome Spring!");
         // Get the user
         ParseUser currentUser = ParseUser.getCurrentUser();
         // Create the comment
         Comment comment = new Comment("Get milk and eggs");
         comment.setPost(post);
         comment.setOwner(currentUser);
         comment.saveInBackground();
         ```
      - (Delete) Delete existing comment
         ```
         myComment.deleteInBackground();
         // After this, the playerName field will be empty
         myComment.remove("comment");
         // Saves the field deletion to your Parse Server
         myComment.saveInBackground();
         ```
      - (Read/GET) Query all posts from all users
         ```swift
         let query = PFQuery(className:"Post")
         query.include(Post.USER);
         query.setLimit(30);
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // 
           allpost.addAll(posts)
           adapter.notifyDatasetChanged();
            }
         }
         ```
   - Post Screen
      - (Create/POST) Create a new post object
         ```
         private void savePost(String description, ParseUser currentUser, File photoFile) {
             Post post = new Post();
             post.setDescription(description);
             post.setImage(new ParseFile(photoFile));
             post.setUser(currentUser);
             post.saveInBackground(new SaveCallback() {
                 @Override
                 public void done(ParseException e) {
                     if (e != null){
                         Log.e(TAG, "Error while saving", e);
                         Toast.makeText(MainActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                     }
                     Log.i(TAG, "Post save was successful!!");
                     etDescription.setText("");
                     ivPostImage.setImageResource(0);
                 }
             });
         }

         ```
   - Chat Screen
   - Profile Screen
      - (Read/GET) Query logged in user object
         ```
         ParseQuery<ParseUser> query = ParseUser.getQuery();
         query.whereKey("user", equalTo: currentUser);
         query.findInBackground(new FindCallback<ParseUser>() {
           public void done(List<ParseUser> objects, ParseException e) {
             if (e == null) {
                 print("Successfully retrieved user.")
             } else {
                 print(error.localizedDescription)
             }
             String userId = item.getUserId();
             String userEmail = item.getUserEmail();
           }
         });
         ```
      - (Read/GET) Query all posts where user is currentUser
         ```swift
         let query = PFQuery(className:"Post")
         query.whereKey("user", equalTo: currentUser);
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // 
           allpost.addAll(posts)
           adapter.notifyDatasetChanged();
            }
         }
         ```
     - (Delete) Delete existing post
         ```
         myPost.deleteInBackground();
         // After this, the playerName field will be empty
         myPost.remove("post");
         // Saves the field deletion to your Parse Server
         myPost.saveInBackground();
         ```
     - (Read/GET) getcurrentUser to check if the user logged out sucessfully
         ```
         ParseUser.logOut();
         ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
         ```
#### [OPTIONAL:] Existing API Endpoints
##### An API of Yelp's Business
- Base URL - [https://api.yelp.com/v3/businesses](https://api.yelp.com/v3/businesses/search)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /search | returns up to 1000 businesses based on the provided search criteria
    `GET`    | /{id} | returns detailed business content
    `GET`    | /{id}/reviews | returns up to three review excerpts for a given business
    
### Completed GIF

<img src="https://github.com/Codepath-group-2/YumSpot/blob/master/final.gif" width=300 />

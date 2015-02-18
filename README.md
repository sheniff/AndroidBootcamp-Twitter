# AndroidBootcamp-TwitterApp
Twitter project for Android Bootcamp at Yahoo!

![twitter1](https://cloud.githubusercontent.com/assets/1939291/6099041/5d8c00a0-afa3-11e4-973c-295b77235ba0.gif)
![twitter2b](https://cloud.githubusercontent.com/assets/1939291/6251148/20a279de-b747-11e4-90f7-4b7d73794ff5.gif)

**User Stories:**
* [x] User can sign in to Twitter using OAuth login
* [x] User can view the tweets from their home timeline
  * User should be displayed the username, name, and body for each tweet
  * User should be displayed the relative timestamp for each tweet "8m", "7h"
  * User can view more tweets as they scroll with infinite pagination
  * Optional: Links in tweets are clickable and will launch the web browser (see autolink)
* [x] User can compose a new tweet
  * User can click a “Compose” icon in the Action Bar on the top right
  * User can then enter a new tweet and post this to twitter
  * User is taken back to home timeline with new tweet visible in timeline
  * Optional: User can see a counter with total number of characters left for tweet

**Advanced Stories:**
* [x] User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
* [x] User can open the twitter app offline and see last loaded tweets
  * Tweets are persisted into sqlite and can be displayed from the local DB
* [x] User can tap a tweet to display a "detailed" view of that tweet
* [x] User can select "reply" from detail view to respond to a tweet
* [x] Improve the user interface and theme the app to feel "twitter branded"

**Bonus Stories:**
* [x] User can see embedded image media within the tweet detail view
* [x] Compose activity is replaced with a modal overlay

### Extending features with fragments :D
**Completed stories**
* [x] Includes all required user stories from Week 3 Twitter Client
* [x] User can switch between Timeline and Mention views using tabs.
   * [x] User can view their home timeline tweets.
   * [x] User can view the recent mentions of their username.
* [x] User can navigate to view their own profile
   * [x] User can see picture, tagline, # of followers, # of following, and tweets on their profile.
* [x] User can click on the profile image in any tweet to see another user's profile.
   * [x] User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
   * [x] Profile view should include that user's timeline
   * [x] Optional: User can view following / followers list through the profile
* [x] User can infinitely paginate any of these timelines (home, mentions, user) by scrolling to the bottom

**Completed advanced stories**
* [x] Advanced: Robust error handling, check if internet is available, handle error cases, network failures
* [x] Advanced: When a network request is sent, user sees an indeterminate progress indicator
* [x] Advanced: User can "reply" to any tweet on their home timeline
   * [x] The user that wrote the original tweet is automatically "@" replied in compose
* [x] Advanced: User can click on a tweet to be taken to a "detail view" of that tweet
   * [x] Advanced: User can take favorite (and unfavorite) or reweet actions on a tweet
* [x] Advanced: Improve the user interface and theme the app to feel twitter branded

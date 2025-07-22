# Stock Monitor Android App
<img align="right" width="300" src="https://github.com/user-attachments/assets/b58159e9-be29-4f0c-bfc8-2a6480b80610" width="700" alt="Header image"/>
This Android application tracks stock quotes and price changes in real-time. It lets users build a personal watchlist, monitor percentage changes, and explore historical price trends through an intuitive UI and home screen widgets.
<br clear="all" />

## ✨ Features
- Add, remove, and manage a personal stock watchlist.
- View live stock quotes with price and percentage change indicators.
- Detailed historical data with charts and trends.
- Background sync jobs for automated updates.
- Home screen widget for instant stock monitoring.
- Offline mode with cached stock data.
- Smooth UI interactions with swipe-to-refresh and swipe-to-delete gestures.

## 🛠️ Tech Stack
- **Language:** Java (Android SDK).
- **UI Components:** RecyclerView, SwipeRefreshLayout, AppCompat, custom widgets.
- **Networking:** Retrofit 2 for API calls.
- **Data Storage:** SQLite database with ContentProvider.
- **Background Tasks:** JobService / JobScheduler for periodic updates.
- **View Binding:** ButterKnife.
- **Charts & History:** Custom history activity with chart display.

## ⚙️ How It Works
- On launch, the app displays a **watchlist** with current stock prices and daily changes.
- Users can **add new stock symbols** through a dialog, and manage their watchlist using swipe-to-remove gestures.
- Real-time data is fetched from an API via **Retrofit** and refreshed periodically using scheduled background jobs.
- A dedicated **History screen** shows detailed price trends and charts for each stock.
- A **home screen widget** provides quick, at-a-glance updates without opening the app.

**StyleMate** 👗

Digitizing your wardrobe, one vibe at a time.
StyleMate is a fashion management app designed to digitize your wardrobe and curate your personal style. It serves as a community platform where users can explore and draw outfit inspiration from others, helping everyone find their perfect look.

📱 **App Screens**

Login & Register: Secure entry using Firebase Authentication with a modern, card-based interface.

Home (Main): The central feed for discovering outfit inspirations and fashion trends from other users. You can filter the feed by specific vibes such as Work, Party, Restaurant, and more to find the exact look you need.

Upload Outfit: A streamlined process to capture your looks, allowing you to tag specific items like Top, Bottom, and Shoes.

Favorites: A personal gallery dedicated to the looks and styles you find most inspiring from the community.

Profile: A personal hub displaying your uploaded collections, profile picture, and account information.

Outfit Details: An in-depth view of any outfit, showcasing metadata (Vibe, Accessories, etc.) and providing deletion options for owners.

**🚀 Technical Architecture & Stack**

This project follows modern Android development standards, emphasizing clean code and scalability:

Language: 100% Kotlin.

BaseActivity Inheritance: Optimized code structure using a BaseActivity to handle shared logic such as Edge-to-Edge display, Navigation, and UI helpers (DRY principle).

Dynamic UI Layouts: Extensive use of ConstraintLayout for responsive design and Material Design 3 components (MaterialCardView, ShapeableImageView).

🛠 Advanced Features & Libraries

Firebase Ecosystem: * Firestore: Real-time NoSQL database for metadata and social sync.

Storage: Cloud hosting for high-fidelity fashion imagery.

Auth: Secure session management.

Image Handling (Glide): Advanced image loading, caching, and transformations to ensure a smooth 60fps scrolling experience in the feed.

Animations (Lottie): Integration of vector-based animations for an interactive and high-quality UX during loading states.

IME & Keyboard Handling: Implementation of WindowInsetsCompat to ensure the UI adjusts gracefully when the keyboard is active, preventing layout clipping.

📸 **UI Design**

The app follows a minimalist, high-contrast design:

Clean Backgrounds: Focus remains on the fashion imagery.

Bold Headers: All-caps typography with distinctive pink visual indicators for brand consistency.

Interactive Navigation: Smooth bottom navigation for quick access to Home, Upload, Favorites, and Profile.


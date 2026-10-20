# Comic Book Portal

A personal project for learning **React Native** and **Expo**.

There's no bigger goal here than building something real while getting comfortable with the React Native ecosystem — file-based routing, native modules, mobile-specific UI patterns, and reading/handling files on-device (in this case, `.cbz` comic book archives).

## What it does (so far)

- Browse and open comic book files
- A test screen for reading `.cbz` archives (`src/app/(tabs)/testReader.tsx`)
- Basic tab navigation via [Expo Router](https://docs.expo.dev/router/introduction)

This is a work in progress and will change shape as I learn.

## Tech stack

- [Expo](https://docs.expo.dev/) (SDK 57) + Expo Router
- React Native
- TypeScript
- NativeWind (Tailwind for React Native)
- `expo-sqlite`, `expo-file-system`, `expo-document-picker` for local storage and file handling
- `fflate` for unzipping `.cbz`/`.zip` comic archives

## Getting started

1. Install dependencies

   ```bash
   npm install
   ```

2. Start the app

   ```bash
   npx expo start
   ```

From there you can open it in a development build, Android emulator, iOS simulator, or Expo Go.

## Notes to self

- Project uses file-based routing — screens live under `src/app/`.
- Expo has changed a lot since older tutorials; when in doubt, check the versioned docs at [docs.expo.dev/versions/v57.0.0](https://docs.expo.dev/versions/v57.0.0/) rather than assuming older APIs still apply.

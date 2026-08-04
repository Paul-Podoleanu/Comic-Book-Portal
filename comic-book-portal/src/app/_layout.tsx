import { Stack } from "expo-router";
import './globals.css'
import ExpoImage from "expo-image/src/ExpoImage";
import {cssInterop} from "nativewind";

cssInterop(ExpoImage, { className: "style" });

export default function RootLayout() {
  return  (
      <Stack>
        <Stack.Screen
            name="(tabs)"
            options={{headerShown: false}}
        />
      </Stack>
  )
}

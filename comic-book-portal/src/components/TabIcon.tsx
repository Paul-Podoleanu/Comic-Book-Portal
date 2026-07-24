import { Image, Text, View } from "react-native";
import { ImageBackground } from "expo-image";
import { cssInterop } from "nativewind";
import { images } from "../../constants/images";
import { LinearGradient } from "expo-linear-gradient";

cssInterop(LinearGradient, { className: "style" });

type TabIconProps = {
    focused: boolean;
    icon: any;
    title: string;
};

export default function TabIcon({ focused, icon, title }: TabIconProps) {
    if (focused) {
        return (
            <LinearGradient
                colors={["#FFCC00", "#FFE88A"]}
                start={{ x: 0, y: 0 }}
                end={{ x: 1, y: 0 }}
                className="flex flex-row w-full h-full min-w-[110px] justify-center items-center rounded-md overflow-hidden"
            >
                <Image source={icon} tintColor="#151312" className="size-5" />
                <Text className="text-secondary text-base font-semibold ml-2">
                    {title}
                </Text>
            </LinearGradient>
        );
    }

    return (
        <View className="size-full justify-center items-center rounded-full">
            <Image source={icon} tintColor="#FFCC00" className="size-5" />
        </View>
    );
}

import { Tabs } from "expo-router";
import { Image } from "react-native";
import { icons } from "../../../constants/icons";
import TabIcon from "../../components/TabIcon";

export default function TabsLayout() {
    return (
        <Tabs
            screenOptions={{
                tabBarShowLabel: false,
                tabBarItemStyle: {
                    width: "100%",
                    height: "100%",
                    justifyContent: "center",
                    alignItems: "center",
                    marginTop: 6,
                },
                tabBarStyle: {
                    backgroundColor: "#0f0d23",
                    borderRadius: 10,
                    marginHorizontal: 20,
                    marginBottom: 36,
                    height: 52,
                    position: "absolute",
                    overflow: "hidden",
                    borderWidth: 1,
                    borderColor: "#0f0d23",
                },
            }}
        >
            <Tabs.Screen
                name="index"
                options={{
                    title: "Home",
                    headerShown: false,
                    tabBarIcon: ({ focused }) => (
                        <TabIcon focused={focused} icon={icons.home} title="Home" />
                    ),
                }}
            />
            <Tabs.Screen
                name="saved"
                options={{
                    title: "Saved",
                    headerShown: false,
                    tabBarIcon: ({ focused }) => (
                        <TabIcon focused={focused} icon={icons.save} title="Saved" />
                    ),
                }}
            />
            <Tabs.Screen
                name="testReader"
                options={{
                    title: "Test Reader",
                    headerShown: false,
                    tabBarIcon: ({ focused }) => (
                        <TabIcon focused={focused} icon={icons.play} title="Test Reader" />
                    ),
                }}
            />
        </Tabs>
    );
}

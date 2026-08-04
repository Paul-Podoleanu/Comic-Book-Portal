import {View, Text, TouchableOpacity} from "react-native";
import {Image} from "expo-image";

const BookCard = ({
    title,
    thumbnail,
    file_path
}: Book) => {
    return (
        <View className="w-[30%]">
            <TouchableOpacity>
            <Image
                source={{uri: thumbnail}}
                className="w-full h-52 rounded-lg"
                contentFit="cover"
            >
            </Image>
            <Text className={"text-xl text-white text-center mt-5 mb-3"}>
                {title}
            </Text>
            </TouchableOpacity>
        </View>
    )
};

export default BookCard;
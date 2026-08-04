import {FlatList, ListRenderItemInfo, Pressable, ScrollView, Text, View} from "react-native";
import './../globals.css'
import { Directory, File, Paths } from "expo-file-system";
import {useState} from "react";
import BookCard from '@/components/BookCard'
import { unzipSync } from "fflate";


export default function Index() {
    const IMAGE_EXT = /\.(jpe?g|png|gif|webp|bmp)$/i;

    const [books, setBooks] = useState<Book[]>([]);

    function findCbzFiles(folder: Directory): File[] {
        return folder
            .list()
            .filter((entry): entry is File => entry instanceof File)
            .filter((file) => /\.cbz$/i.test(file.name));
    }

    function naturalCompare(a: string, b: string) {
        return a.localeCompare(b, undefined, { numeric: true, sensitivity: "base" });
    }

    async function extractThumbnail(cbzFile: File): Promise<string> {
        const bytes = await cbzFile.bytes();
        const entries = unzipSync(bytes);

        const firstImageName = Object.keys(entries)
            .filter((name) => !name.endsWith("/") && IMAGE_EXT.test(name))
            .sort(naturalCompare)[0];

        if (!firstImageName) return "";

        const thumbDir = new Directory(Paths.cache, "thumbnails");
        if (!thumbDir.exists) thumbDir.create({ intermediates: true });

        const ext = firstImageName.match(IMAGE_EXT)?.[0] ?? ".jpg";
        const thumbFile = new File(thumbDir, `${cbzFile.name}-thumb${ext}`);
        thumbFile.write(entries[firstImageName]);

        return thumbFile.uri;
    }

    async function pickFolder () {
        try {
            const folder = await Directory.pickDirectoryAsync();
            const cbzFiles = findCbzFiles(folder);

            const detectedBooks: Book[] = await Promise.all(
                cbzFiles.map(async (file) => ({
                    title: file.name.replace(/\.cbz$/i, ""),
                    thumbnail: await extractThumbnail(file),
                    file_path: file.uri,
                }))
            );

            setBooks(detectedBooks);
        } catch (err) {
            console.error("pickFolder failed:", err);
        }
    };

    return (
        <View className={"flex-1 bg-black"}>
            <Text className={"text-xl text-white text-center mt-5 mb-3"}>The comic portal</Text>
            <View className="pt-16 px-5 pb-3">
            <Pressable
                onPress={pickFolder}
                className="bg-[#FFCC00] rounded-md py-3 items-center"
                //disabled={loading}
            >
                <Text className="text-[#151312] font-semibold items-center">
                    {"Pick a folder"}
                </Text>
            </Pressable>
            </View>
            <ScrollView>
                <FlatList
                    data={books}
                    renderItem={({item}: ListRenderItemInfo<Book>) => <BookCard {...item} />}
                    keyExtractor={(item: Book) => item.title}
                    numColumns={3}
                    columnWrapperStyle={{
                        justifyContent: "flex-start",
                        gap: 20,
                        paddingRight: 5,
                        marginBottom: 10,
                    }}
                    className="mt-2 pb-32"
                    scrollEnabled={false}
                />
            </ScrollView>
        </View>
    );
}

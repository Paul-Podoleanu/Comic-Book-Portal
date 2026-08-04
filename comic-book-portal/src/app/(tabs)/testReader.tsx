import { useCallback, useState } from "react";
import {
    ActivityIndicator,
    Alert,
    Dimensions,
    Pressable,
    ScrollView,
    Text,
    View,
} from "react-native";
import { Image } from "expo-image";
import * as DocumentPicker from "expo-document-picker";
import * as LegacyFileSystem from "expo-file-system/legacy";
import { Directory, File, Paths } from "expo-file-system";
import { unzipSync } from "fflate";
import "./../globals.css";

const IMAGE_EXT = /\.(jpe?g|png|gif|webp|bmp)$/i;
const SCREEN_WIDTH = Dimensions.get("window").width;

function naturalCompare(a: string, b: string) {
    return a.localeCompare(b, undefined, { numeric: true, sensitivity: "base" });
}

export default function TestReader() {
    const [pages, setPages] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);
    const [comicName, setComicName] = useState<string | null>(null);
    const [pageIndex, setPageIndex] = useState(0);

    const pickAndOpen = useCallback(async () => {
        const result = await DocumentPicker.getDocumentAsync({
            type: "*/*",
            copyToCacheDirectory: true,
        });
        if (result.canceled || !result.assets?.length) return;

        const asset = result.assets[0];
        if (!/\.cbz$/i.test(asset.name)) {
            Alert.alert("Not a CBZ file", `"${asset.name}" doesn't look like a .cbz archive.`);
            return;
        }

        setLoading(true);
        setPages([]);
        setPageIndex(0);

        try {
            // The picker's URI can be a content:// SAF URI that the new File API
            // can't open directly, so copy it into our sandbox with the legacy API
            // first. Everything after that is a plain file:// path we own, so we
            // can read/write it as raw bytes (no base64) with the new File API.
            const archiveUri = `${LegacyFileSystem.cacheDirectory}picked.cbz`;
            await LegacyFileSystem.copyAsync({ from: asset.uri, to: archiveUri });

            const archive = new File(archiveUri);
            const bytes = await archive.bytes();
            const entries = unzipSync(bytes);

            const imageNames = Object.keys(entries)
                .filter((name) => !name.endsWith("/") && IMAGE_EXT.test(name))
                .sort(naturalCompare);

            if (imageNames.length === 0) {
                Alert.alert("Empty archive", "No image pages were found inside this CBZ.");
                setLoading(false);
                return;
            }

            const outDir = new Directory(Paths.cache, "cbz-test");
            if (outDir.exists) outDir.delete();
            outDir.create({ intermediates: true });

            const uris = imageNames.map((name, i) => {
                const ext = name.match(IMAGE_EXT)?.[0] ?? ".jpg";
                const outFile = new File(outDir, `page-${i}${ext}`);
                outFile.write(entries[name]);
                return outFile.uri;
            });

            setComicName(asset.name);
            setPages(uris);
        } catch (err) {
            console.error(err);
            Alert.alert("Failed to open CBZ", String(err));
        } finally {
            setLoading(false);
        }
    }, []);

    return (
        <View className="flex-1 bg-black">
            <View className="pt-16 px-5 pb-3">
                <Text className="text-white text-xl font-bold mb-3">Test Reader</Text>
                <Pressable
                    onPress={pickAndOpen}
                    className="bg-[#FFCC00] rounded-md py-3 items-center"
                    disabled={loading}
                >
                    <Text className="text-[#151312] font-semibold items-center">
                        {loading ? "Opening..." : "Pick a .cbz file"}
                    </Text>
                </Pressable>
                {comicName && (
                    <Text className="text-white/70 mt-10 text-center" numberOfLines={1}>
                        page {pageIndex + 1} / {pages.length}
                    </Text>
                )}
            </View>

            {loading && (
                <View className="flex-1 items-center justify-center">
                    <ActivityIndicator color="#FFCC00" size="large" />
                </View>
            )}

            {!loading && pages.length > 0 && (
                <ScrollView
                    horizontal
                    pagingEnabled
                    showsHorizontalScrollIndicator={false}
                    onMomentumScrollEnd={(e) => {
                        const index = Math.round(
                            e.nativeEvent.contentOffset.x / SCREEN_WIDTH
                        );
                        setPageIndex(index);
                    }}
                >
                    {pages.map((uri) => (
                        <View
                            key={uri}
                            style={{ width: SCREEN_WIDTH }}
                            className="flex-1 items-center justify-center"
                        >
                            <Image
                                source={{ uri }}
                                style={{ width: "100%", height: "100%" }}
                                contentFit="contain"
                            />
                        </View>
                    ))}
                </ScrollView>
            )}

            {!loading && pages.length === 0 && (
                <View className="flex-1 items-center justify-center px-8">
                    <Text className="text-white/50 text-center">
                        Pick a .cbz file above to test reading and page rendering.
                    </Text>
                </View>
            )}
        </View>
    );
}

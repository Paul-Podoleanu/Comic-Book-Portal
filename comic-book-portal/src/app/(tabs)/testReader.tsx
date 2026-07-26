import { useCallback, useState } from "react";
import { ActivityIndicator, Alert, Pressable, Text, View } from "react-native";
import { Image } from "expo-image";
import PagerView from "react-native-pager-view";
import * as DocumentPicker from "expo-document-picker";
import * as FileSystem from "expo-file-system/legacy";
import { unzipSync } from "fflate";
import "./../globals.css";

const IMAGE_EXT = /\.(jpe?g|png|gif|webp|bmp)$/i;

const B64_CHARS =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

function bytesToBase64(bytes: Uint8Array): string {
    let out = "";
    for (let i = 0; i < bytes.length; i += 3) {
        const b0 = bytes[i];
        const b1 = bytes[i + 1];
        const b2 = bytes[i + 2];
        out += B64_CHARS[b0 >> 2];
        out += B64_CHARS[((b0 & 0x03) << 4) | (b1 === undefined ? 0 : b1 >> 4)];
        out += b1 === undefined ? "=" : B64_CHARS[((b1 & 0x0f) << 2) | (b2 === undefined ? 0 : b2 >> 6)];
        out += b2 === undefined ? "=" : B64_CHARS[b2 & 0x3f];
    }
    return out;
}

function base64ToBytes(base64: string): Uint8Array {
    const clean = base64.replace(/=+$/, "");
    const bytes: number[] = [];
    let buffer = 0;
    let bits = 0;
    for (let i = 0; i < clean.length; i++) {
        buffer = (buffer << 6) | B64_CHARS.indexOf(clean[i]);
        bits += 6;
        if (bits >= 8) {
            bits -= 8;
            bytes.push((buffer >> bits) & 0xff);
        }
    }
    return new Uint8Array(bytes);
}

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
            const cacheDir = FileSystem.cacheDirectory!;
            const archiveUri = `${cacheDir}picked.cbz`;

            await FileSystem.copyAsync({ from: asset.uri, to: archiveUri });

            const base64 = await FileSystem.readAsStringAsync(archiveUri, {
                encoding: FileSystem.EncodingType.Base64,
            });
            const bytes = base64ToBytes(base64);
            const entries = unzipSync(bytes);

            const imageNames = Object.keys(entries)
                .filter((name) => !name.endsWith("/") && IMAGE_EXT.test(name))
                .sort(naturalCompare);

            if (imageNames.length === 0) {
                Alert.alert("Empty archive", "No image pages were found inside this CBZ.");
                setLoading(false);
                return;
            }

            const outDir = `${cacheDir}cbz-test/`;
            await FileSystem.deleteAsync(outDir, { idempotent: true });
            await FileSystem.makeDirectoryAsync(outDir, { intermediates: true });

            const uris: string[] = [];
            for (let i = 0; i < imageNames.length; i++) {
                const name = imageNames[i];
                const ext = name.match(IMAGE_EXT)?.[0] ?? ".jpg";
                const pageUri = `${outDir}page-${i}${ext}`;
                await FileSystem.writeAsStringAsync(pageUri, bytesToBase64(entries[name]), {
                    encoding: FileSystem.EncodingType.Base64,
                });
                uris.push(pageUri);
            }

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
                    <Text className="text-[#151312] font-semibold">
                        {loading ? "Opening..." : "Pick a .cbz file"}
                    </Text>
                </Pressable>
                {comicName && (
                    <Text className="text-white/70 mt-2" numberOfLines={1}>
                        {comicName} — page {pageIndex + 1} / {pages.length}
                    </Text>
                )}
            </View>

            {loading && (
                <View className="flex-1 items-center justify-center">
                    <ActivityIndicator color="#FFCC00" size="large" />
                </View>
            )}

            {!loading && pages.length > 0 && (
                <PagerView
                    className="flex-1"
                    initialPage={0}
                    onPageSelected={(e) => setPageIndex(e.nativeEvent.position)}
                >
                    {pages.map((uri) => (
                        <View key={uri} className="flex-1 items-center justify-center">
                            <Image
                                source={{ uri }}
                                style={{ width: "100%", height: "100%" }}
                                contentFit="contain"
                            />
                        </View>
                    ))}
                </PagerView>
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

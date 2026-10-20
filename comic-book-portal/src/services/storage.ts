import AsyncStorage from "@react-native-async-storage/async-storage";

const FOLDER_STORAGE_KEY = "comicFolderUris";

export async function saveFolderUri(uri: string) {
    const existing = await AsyncStorage.getItem(FOLDER_STORAGE_KEY);
    const uris: string[] = existing ? JSON.parse(existing) : [];
    if (!uris.includes(uri)) {
        uris.push(uri);
        await AsyncStorage.setItem(FOLDER_STORAGE_KEY, JSON.stringify(uris));
    }
}

export async function loadFolderUris(): Promise<string[]> {
    const existing = await AsyncStorage.getItem(FOLDER_STORAGE_KEY);
    return existing ? JSON.parse(existing) : [];
}

export async function clearFolderUris(): Promise<string[]> {
    const existing = await AsyncStorage.getItem(FOLDER_STORAGE_KEY);
    return existing ? JSON.parse(existing) : [];
}
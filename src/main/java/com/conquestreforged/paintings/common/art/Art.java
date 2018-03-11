package com.conquestreforged.paintings.common.art;

import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public interface Art {

    String DATA_TAG = "Painting";
    String TYPE_TAG = "TypeID";
    String ART_TAG = "ArtID";

    int u();

    int v();

    int width();

    int height();

    int textureWidth();

    int textureHeight();

    String getName();

    String getDisplayName(String parent);

    boolean matches(Enum art);

    static int indexOf(Enum art, List<Art> list) {
        for (int i = 0; i < list.size(); i++) {
            Art a = list.get(i);
            if (a.matches(art)) {
                return i;
            }
        }
        return 0;
    }

    static boolean valid(String name, List<Art> list) {
        for (Art art : list) {
            if (art.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
